/*
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
 */

package com.futurewei.everest;

import com.futurewei.everest.connector.EverestCollectorConsumer;
import com.futurewei.everest.datatypes.EverestCollectorData;
import com.futurewei.everest.functions.*;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;

import java.util.Properties;

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
		final String cgTopic = params.get("cg-topic", EverestDefaultValues.KAFKA_CG);
		final String outputLTopic = params.get("out-l-topic", EverestDefaultValues.KAFKA_OUTPUT_LOW_TOPIC);
		final String outputRTopic = params.get("out-r-topic", EverestDefaultValues.KAFKA_OUTPUT_REGULAR_TOPIC);
        final String outputHTopic = params.get("out-h-topic", EverestDefaultValues.KAFKA_OUTPUT_HIGH_TOPIC);
        final String outputCTopic = params.get("out-c-topic", EverestDefaultValues.KAFKA_OUTPUT_CRITICAL_TOPIC);
        final String bootstrapServers = params.get("bootstrap.servers", EverestDefaultValues.BOOTSTRAP_SERVERS);
        final int windowSize = Integer.parseInt(params.get("window-size", EverestDefaultValues.WINDOW_SIZE)); //in secs

		// set up the streaming execution environment
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.getConfig().setRestartStrategy(RestartStrategies.fixedDelayRestart(4, 10000));
        env.enableCheckpointing(5000); // create a checkpoint every 5 seconds
        env.getConfig().setGlobalJobParameters(params); // make parameters available in the web interface
		// prepare to process with event time
		env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        // configure the Kafka
//        Properties kafkaProps = new Properties();
//        kafkaProps.setProperty("bootstrap.servers", bootstrapServers);

        // start capturing data from Kafka

        DataStream<EverestCollectorData> everestCollectorDataStream = getDataFromKafka(env, bootstrapServers, cgTopic, everestDataTopic);

        DataStream<EverestCollectorData> everestCollectorDataStreamByKey = everestCollectorDataStream
                .assignTimestampsAndWatermarks(new CustomWatermarkExtractor()).name("F_TimeStampsWatermark")
                .keyBy("cluster_id");

        // cleansing/filter out the data which has negative disk usage or greater than 100
        DataStream<EverestCollectorData> cleansedEverestCollectorDataStream = everestCollectorDataStreamByKey
                // filter out the elements that have values < Low bound or > high bound
                .filter(new ValidValueFilter()).name("F_ValidValue_Filter");

//        DataStream<KafkaEvent<String, Double, Double>> maxValueStream = cleansedDataStream.
//                assignTimestampsAndWatermarks(new CustomWatermarkExtractor()).name("So_Kafka_In_From_" + ).
//                keyBy("id").
//                window(TumblingEventTimeWindows.of(Time.seconds(windowSize))).
//                reduce(new MaxDiffReducer()).name("F_MaxDiffReducer");
//
//        // split the stream into 'low', 'high', 'critical' stream
//        SplitStream<KafkaEvent<String, Double, Double>> splittedSensorStream = maxValueStream.split(new ValueSplitter());
//
//        // select the streams and group the stream by its usage category
//        DataStream<KafkaEvent<String, Double, Double>> lowValue = splittedSensorStream.select("low");
//        DataStream<KafkaEvent<String, Double, Double>> highValue = splittedSensorStream.select("high");
//        DataStream<KafkaEvent<String, Double, Double>> criticalValue = splittedSensorStream.select("critical");
//
//        // write the info into Kafka and InfluxDB for visualization
//        criticalValue.addSink(
//                new FlinkKafkaProducer010<KafkaEvent<String, Double, Double>>(
//                        outputCTopic,
//                        new KafkaEventSchema(),
//                        kafkaProps)).name("Si_Kafka_Out_To_" + outputCTopic);
//        // write the info into Kafka
//        highValue.addSink(
//                new FlinkKafkaProducer010<KafkaEvent<String, Double, Double>>(
//                        outputHTopic,
//                        new KafkaEventSchema(),
//                        kafkaProps)).name("Si_Kafka_Out_To_" + outputHTopic);
//
//        // write the low disk usage into Kafka
//        lowValue.
//                addSink(
//                new FlinkKafkaProducer010<KafkaEvent<String, Double, Double>>(
//                        outputLTopic,
//                        new KafkaEventSchema(),
//                        kafkaProps)).name("Si_Kafka_Out_To_" + outputLTopic);

		// execute program
		env.execute("Flink Everest Job");
	}

    private static DataStream<EverestCollectorData> getDataFromKafka(StreamExecutionEnvironment env, String kafkaAddress, String kafkaGroup, String inputTopic) {
        // input from a stream of string in the format of ID,SENSOR_VALUE,TIMESTAMP,DESCRIPTION
        // ID is used as the key

        FlinkKafkaConsumer010<EverestCollectorData> consumer = EverestCollectorConsumer.createEverestCollectorDataConsumer(inputTopic, kafkaAddress, kafkaGroup);
        return env.addSource(consumer).name("So_Kafka_"+inputTopic);

    }
}

