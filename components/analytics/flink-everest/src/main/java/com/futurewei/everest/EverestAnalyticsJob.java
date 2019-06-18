/*
 * Copyright 2018-2019 The Everest Authors
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */

package com.futurewei.everest;

import com.futurewei.everest.connector.EverestCollectorConsumer;
import com.futurewei.everest.datatypes.EverestCollectorData;
import com.futurewei.everest.datatypes.EverestCollectorDataT;
import com.futurewei.everest.datatypes.EverestCollectorTSerializationSchema;
import com.futurewei.everest.datatypes.EverestCollectorTrace;
import com.futurewei.everest.functions.*;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer010;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.apache.flink.streaming.connectors.kafka.config.StartupMode.LATEST;

/**
 *
 * Implementation of data pipeline job in Everest demo
 * I generate sample disk usage (in percentage) from outside kubernetes and feed it into Kafka topic 'in-topic'
 * The job will take it as source, sanitize it (greater or equal 0 and less than or equal 100)
 * The stream will be keyed by 'id' and sampled in 5 seconds window.
 * it then take the max
 *
 *
 * Parameters:
 *   --cg-topic Kafka Topic Consumer Group for incoming data
 *   --cpu-topic Kafka Topic for injection of the cpu data
 *   --mem-topic Kafka Topic for injection of the memory data
 *   --net-topic Kafka Topic for injection of the network data
 *   --clu-topic Kafka Topic for injection of the cluster data
 *   --out-l-topic Kafka Topic to store low load
 *   --out-h-topic Kafka Topic to store high load
 *   --out-c-topic Kafka Topic to store critical critical
 *   --bootstrap.servers Kafka Broker URL
 *   --window-size how many seconds of event time the data will be collected and triggered evaluation
 *
 *
 * <p>For a tutorial how to write a Flink streaming application, check the
 * tutorials and examples on the <a href="http://flink.apache.org/docs/stable/">Flink Website</a>.
 *
 * <p>To package your application into a JAR file for execution, run
 * 'mvn clean package' on the command line.
 *
 * <p>If you change the name of the main class (with the public static void main(String[] args))
 * method, change the respective entry in the POM.xml file (simply search for 'mainClass').
 */

public class EverestAnalyticsJob {

	public static void main(String[] args) throws Exception {
        // parse the arguments
		final ParameterTool params = ParameterTool.fromArgs(args);
		final String everestDataTopic = params.get("everest-data-topic", EverestDefaultValues.KAFKA_EVEREST_DATA_TOPIC);
		final String everestTraceTopic = params.get("everest-trace-topic", EverestDefaultValues.KAFKA_EVEREST_TRACE_TOPIC);
		final String cgTopic = params.get("cg-topic", EverestDefaultValues.KAFKA_CG);
		final String outputCpuCTopic = params.get("cpu-c-topic", EverestDefaultValues.KAFKA_OUTPUT_CPU_C_TOPIC);
        final String outputCpuHTopic = params.get("cpu-h-topic", EverestDefaultValues.KAFKA_OUTPUT_CPU_H_TOPIC);
        final String outputNetLTopic = params.get("net-l-topic", "net-l-topic");
        final String outputMemCTopic = params.get("mem-c-topi c", EverestDefaultValues.KAFKA_OUTPUT_MEM_C_TOPIC);
        final String outputMemHTopic = params.get("mem-h-topic", EverestDefaultValues.KAFKA_OUTPUT_MEM_H_TOPIC);
        final String outputNetCTopic = params.get("net-c-topic", EverestDefaultValues.KAFKA_OUTPUT_NET_C_TOPIC);
        final String outputNetHTopic = params.get("net-h-topic", EverestDefaultValues.KAFKA_OUTPUT_NET_H_TOPIC);
        final String bootstrapServers = params.get("bootstrap.servers", EverestDefaultValues.BOOTSTRAP_SERVERS);
        final int concurency = Integer.parseInt(params.get("concurency", EverestDefaultValues.CONCURENCY));
        final int windowSize = Integer.parseInt(params.get("window-size", EverestDefaultValues.WINDOW_SIZE)); //in secs

		// set up the streaming execution environment
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.getConfig().setRestartStrategy(RestartStrategies.fixedDelayRestart(4, 10000));
        env.enableCheckpointing(5000); // create a checkpoint every 5 seconds
        env.getConfig().setGlobalJobParameters(params); // make parameters available in the web interface
		// prepare to process with event time
		env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
		env.setParallelism(concurency);

		// setup for Prometheus metrics


        // configure the Kafka
        Properties kafkaProps = new Properties();
        kafkaProps.setProperty("bootstrap.servers", bootstrapServers);
        kafkaProps.setProperty("group.id", cgTopic);

        DataStream<EverestCollectorTrace> everestCollectorTraceStream = getTraceFromKafka(env, everestTraceTopic, kafkaProps);
        DataStream<EverestCollectorData> everestCollectorDataStream = getDataFromKafka(env, everestDataTopic, kafkaProps);

        /**
         * Transform by sorting between CPU and MEM data
         */
        DataStream<EverestCollectorDataT<Double, Double>> cpuDataStream = everestCollectorDataStream.flatMap(
                new ValueFlatMap(EverestDefaultValues.TYPE_TO_COLLECT_CPU)
        );

        DataStream<EverestCollectorDataT<Double, Double>> memDataStream = everestCollectorDataStream.flatMap(
                new ValueFlatMap(EverestDefaultValues.TYPE_TO_COLLECT_MEM)
        );
        DataStream<EverestCollectorDataT<Double, Double>> netDataStream = everestCollectorDataStream.flatMap(
                new ValueFlatMap(EverestDefaultValues.TYPE_TO_COLLECT_NET)
        );

        /**
         * Transform by
         * - extracting the timestamp
         * - validating the range of valid data
         * - keying the data based on containerName
         * - collecting the data in the duration of window time
         * - choosing the maximum value during the window time
         * - storing all the tranformed data into the variable to be processed
         */
        DataStream<EverestCollectorDataT<Double, Double>> cpuDataStreamByKey = cpuDataStream
                .assignTimestampsAndWatermarks(new CustomWatermarkExtractor()).name("F_TimeStampsWatermark_CPU")
                // filter out the elements that have values < Low bound or > high bound
                .filter(new ValidValueFilter(EverestDefaultValues.TYPE_TO_COLLECT_CPU)).name("F_ValidValue_Filter_CPU")
                .keyBy("containerName")
//                .window(TumblingEventTimeWindows.of(Time.seconds(windowSize)))
//                .reduce(new MaxValueReducer()).name("F_MaxValueReducer");
                ;

        DataStream<EverestCollectorDataT<Double, Double>> memDataStreamByKey = memDataStream
                .assignTimestampsAndWatermarks(new CustomWatermarkExtractor()).name("F_TimeStampsWatermark_MEM")
                // filter out the elements that have values < Low bound or > high bound
                .filter(new ValidValueFilter(EverestDefaultValues.TYPE_TO_COLLECT_MEM)).name("F_ValidValue_Filter_MEM")
                .keyBy("containerName")
                //                .window(TumblingEventTimeWindows.of(Time.seconds(windowSize)))
                //                .reduce(new MaxValueReducer()).name("F_MaxValueReducer");
                ;
        DataStream<EverestCollectorDataT<Double, Double>> netDataStreamByKey = netDataStream
                .assignTimestampsAndWatermarks(new CustomWatermarkExtractor()).name("F_TimeStampsWatermark_NET")
                // filter out the elements that have values < Low bound or > high bound
                .filter(new ValidValueFilter(EverestDefaultValues.TYPE_TO_COLLECT_NET)).name("F_ValidValue_Filter_NET")
                .keyBy("containerName")
                //                .window(TumblingEventTimeWindows.of(Time.seconds(windowSize)))
                //                .reduce(new MaxValueReducer()).name("F_MaxValueReducer");
                ;

        /**
         * Now it is time to analyze and categorize the data:
         * - Categorize/Filter/Select/Split the data into 'low', 'regular', 'high', 'critical' stream
         * - The logic/algorithm is inside the CategoryFilter Class
         */
        DataStream<EverestCollectorDataT<Double, Double>> cpuCriticalDataStream = cpuDataStreamByKey
                // filter out the elements that have values critical
                .filter(new CategoryFilter(EverestDefaultValues.CATEGORY_CPU_CRITICAL)).name("F_Category_Filter_Critical_CPU");
        DataStream<EverestCollectorDataT<Double, Double>> cpuHighDataStream = cpuDataStreamByKey
                // filter out the elements that have values high
                .filter(new CategoryFilter(EverestDefaultValues.CATEGORY_CPU_HIGH)).name("F_Category_Filter_High_CPU");
        DataStream<EverestCollectorDataT<Double, Double>> cpuRegularDataStream = cpuDataStreamByKey
                // filter out the elements that have values regular
                .filter(new CategoryFilter(EverestDefaultValues.CATEGORY_CPU_REGULAR)).name("F_Category_Filter_Regular_CPU");
        DataStream<EverestCollectorDataT<Double, Double>> cpuLowDataStream = cpuDataStreamByKey
                // filter out the elements that have values loiw
                .filter(new CategoryFilter(EverestDefaultValues.CATEGORY_CPU_LOW)).name("F_Category_Filter_Low_CPU");
        DataStream<EverestCollectorDataT<Double, Double>> memCriticalDataStream = memDataStreamByKey
                // filter out the elements that have values critical
                .filter(new CategoryFilter(EverestDefaultValues.CATEGORY_MEM_CRITICAL)).name("F_Category_Filter_Critical_MEM");
        DataStream<EverestCollectorDataT<Double, Double>> memHighDataStream = memDataStreamByKey
                // filter out the elements that have values high
                .filter(new CategoryFilter(EverestDefaultValues.CATEGORY_MEM_HIGH)).name("F_Category_Filter_High_MEM");
        DataStream<EverestCollectorDataT<Double, Double>> memRegularDataStream = memDataStreamByKey
                // filter out the elements that have values regular
                .filter(new CategoryFilter(EverestDefaultValues.CATEGORY_MEM_REGULAR)).name("F_Category_Filter_Regular_MEM");
        DataStream<EverestCollectorDataT<Double, Double>> memLowDataStream = memDataStreamByKey
                // filter out the elements that have values loiw
                .filter(new CategoryFilter(EverestDefaultValues.CATEGORY_MEM_LOW)).name("F_Category_Filter_Low_MEM");
        DataStream<EverestCollectorDataT<Double, Double>> netCriticalDataStream = netDataStreamByKey
                // filter out the elements that have values critical
                .filter(new CategoryFilter(EverestDefaultValues.CATEGORY_NET_CRITICAL)).name("F_Category_Filter_Critical_NET");
        DataStream<EverestCollectorDataT<Double, Double>> netHighDataStream = netDataStreamByKey
                // filter out the elements that have values high
                .filter(new CategoryFilter(EverestDefaultValues.CATEGORY_NET_HIGH)).name("F_Category_Filter_High_NET");
        DataStream<EverestCollectorDataT<Double, Double>> netRegularDataStream = netDataStreamByKey
                // filter out the elements that have values regular
                .filter(new CategoryFilter(EverestDefaultValues.CATEGORY_NET_REGULAR)).name("F_Category_Filter_Regular_NET");
        DataStream<EverestCollectorDataT<Double, Double>> netLowDataStream = netDataStreamByKey
                // filter out the elements that have values loiw
                .filter(new CategoryFilter(EverestDefaultValues.CATEGORY_NET_LOW)).name("F_Category_Filter_Low_NET");


        /**
         * write the category data/info back into Kafka
         * from there the command/control module will take actions
         */

        cpuCriticalDataStream.addSink(
                new FlinkKafkaProducer010<EverestCollectorDataT<Double, Double>>(
                        outputCpuCTopic,
                        new EverestCollectorTSerializationSchema(),
                        kafkaProps)).name("Si_CPU_Cricital_Kafka_Out_To_" + outputCpuCTopic);
        // write the info back into Kafka
        cpuHighDataStream.addSink(
                new FlinkKafkaProducer010<EverestCollectorDataT<Double, Double>>(
                        outputCpuHTopic,
                        new EverestCollectorTSerializationSchema(),
                        kafkaProps)).name("Si_CPU_High_Kafka_Out_To_" + outputCpuHTopic);

        // JUST FOR DEBUGGING write the info back into Kafka
/*
        netLowDataStream.addSink(
                new FlinkKafkaProducer010<EverestCollectorDataT<Double, Double>>(
                        outputNetLTopic,
                        new EverestCollectorTSerializationSchema(),
                        kafkaProps)).name("Si_NET_Low_Kafka_Out_To_" + outputNetLTopic);
*/

        // write the cpu/mem usage into Kafka
        memCriticalDataStream.
                addSink(
                        new FlinkKafkaProducer010<EverestCollectorDataT<Double, Double>>(
                                outputMemCTopic,
                                new EverestCollectorTSerializationSchema(),
                                kafkaProps)).name("Si_MEM_Critical_Kafka_Out_To_" + outputMemCTopic);
        // write the low cpu/mem usage into Kafka
        memHighDataStream.
                addSink(
                        new FlinkKafkaProducer010<EverestCollectorDataT<Double, Double>>(
                                outputMemHTopic,
                                new EverestCollectorTSerializationSchema(),
                                kafkaProps)).name("Si_MEM_High_Kafka_Out_To_" + outputMemHTopic);

        // write the cpu/mem usage into Kafka
        netCriticalDataStream.
                addSink(
                        new FlinkKafkaProducer010<EverestCollectorDataT<Double, Double>>(
                                outputNetCTopic,
                                new EverestCollectorTSerializationSchema(),
                                kafkaProps)).name("Si_NET_Critical_Kafka_Out_To_" + outputNetCTopic);
        // write the low cpu/net usage into Kafka
        netHighDataStream.
                addSink(
                        new FlinkKafkaProducer010<EverestCollectorDataT<Double, Double>>(
                                outputNetHTopic,
                                new EverestCollectorTSerializationSchema(),
                                kafkaProps)).name("Si_NET_High_Kafka_Out_To_" + outputNetHTopic);

        // execute program
		env.execute("Flink Everest Job");
	}

	private static DataStream<EverestCollectorData> getDataFromKafka(StreamExecutionEnvironment env, String inputTopic, Properties properties) {
	        // input from a stream prometheus info
        FlinkKafkaConsumer010<EverestCollectorData> consumer = EverestCollectorConsumer.createEverestCollectorDataConsumer(inputTopic, properties, LATEST);
        return env.addSource(consumer).name("So_Kafka_" + inputTopic);
	}
	private static DataStream<EverestCollectorTrace> getTraceFromKafka(StreamExecutionEnvironment env, String inputTopic, Properties properties) {
        // input from a stream tracing info
        FlinkKafkaConsumer010<EverestCollectorTrace> consumer = EverestCollectorConsumer.createEverestCollectorTraceConsumer(inputTopic, properties, LATEST);
        return env.addSource(consumer).name("So_Kafka_" + inputTopic);
	}
}

