package functions;

import com.futurewei.everest.EverestDefaultValues;
import com.futurewei.everest.datatypes.KafkaEvent;
import com.futurewei.everest.functions.CustomWatermarkExtractor;
import com.futurewei.everest.functions.MaxDiffReducer;
import com.futurewei.everest.functions.ValueSplitter;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SplitStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/** 
* ValidValueFilter Tester.
* 
* @author Indra G Harijono
* @since <pre>Nov 11, 2018</pre> 
* @version 1.0 
*/ 
public class Functions_IT_Test {

    /**
    *
    * Method: ValidValueFilter(KafkaEvent kafkaEvent)
    *
    */
    @Test
    public void testValidValueFilter() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // configure your test environment
        env.setParallelism(1);
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        CollectSink sink = new CollectSink();
        // values are collected in a static variable
        sink.values.clear();

        // create a stream of custom elements and apply filter transformations
        KafkaEvent testKafkaEvent0 = new KafkaEvent(0, EverestDefaultValues.VALID_VALUE_LOW_BOUND+20,
                EverestDefaultValues.VALID_VALUE_LOW_BOUND+20, 1541907744041835L, "description");
        KafkaEvent testKafkaEvent1 = new KafkaEvent(0, EverestDefaultValues.VALID_VALUE_HIGH_BOUND-20,
                EverestDefaultValues.VALID_VALUE_HIGH_BOUND-20, 1541907744041835L, "description");
        KafkaEvent testKafkaEvent2 = new KafkaEvent(0, EverestDefaultValues.VALID_VALUE_LOW_BOUND+20,
                EverestDefaultValues.VALID_VALUE_HIGH_BOUND-20, 1541907744041835L, "description");
        KafkaEvent testKafkaEvent3 = new KafkaEvent(0, EverestDefaultValues.VALID_VALUE_HIGH_BOUND-20,
                EverestDefaultValues.VALID_VALUE_LOW_BOUND+20, 1541907744041835L, "description");

        DataStream<KafkaEvent<String, Double, Double>> events = env.fromElements(testKafkaEvent0, testKafkaEvent1, testKafkaEvent2,
                testKafkaEvent3);

        events.addSink(sink);

        // execute
        env.execute();

        // verify your results
        List<KafkaEvent> resultedKafkaEvents = sink.values;
        assertEquals(4, resultedKafkaEvents.size());

    }


    /**
     *
     * Method: MaxDiffReducer(KafkaEvent kafkaEvent)
     *
     */


    @Ignore
    public void testMaxDiffReducer() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // configure your test environment
        env.setParallelism(1);
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        CollectSink sink = new CollectSink();
        // values are collected in a static variable
        sink.values.clear();

        // create a stream of custom elements and apply reduce transformations
        KafkaEvent testKafkaEvent0 = new KafkaEvent(0, EverestDefaultValues.VALID_VALUE_LOW_BOUND+20,
                EverestDefaultValues.VALID_VALUE_LOW_BOUND+20, 1541907744041835L, "description");
        KafkaEvent testKafkaEvent1 = new KafkaEvent(0, EverestDefaultValues.VALID_VALUE_HIGH_BOUND-20,
                EverestDefaultValues.VALID_VALUE_HIGH_BOUND-20, 1541907744041836L, "description");
        KafkaEvent testKafkaEvent2 = new KafkaEvent(0, EverestDefaultValues.VALID_VALUE_LOW_BOUND+20,
                EverestDefaultValues.VALID_VALUE_HIGH_BOUND-20, 1541907744041837L, "description");
        KafkaEvent testKafkaEvent3 = new KafkaEvent(0, EverestDefaultValues.VALID_VALUE_HIGH_BOUND-20,
                EverestDefaultValues.VALID_VALUE_LOW_BOUND+20, 1541907744041838L, "description");

        DataStream<KafkaEvent<String, Double, Double>> events = env.fromElements(testKafkaEvent0, testKafkaEvent1, testKafkaEvent2,
                testKafkaEvent2, testKafkaEvent3);

        DataStream<KafkaEvent<String, Double, Double>> reduceValueStream = events.
                assignTimestampsAndWatermarks(new CustomWatermarkExtractor()).
                keyBy("id").
                window(TumblingEventTimeWindows.of(Time.seconds(50))).
                reduce(new MaxDiffReducer()).name("F_MaxDiffReducer");
        reduceValueStream.addSink(sink);


        // execute
        env.execute();

        // verify your results
        List<KafkaEvent> resultedReducedKafkaEvents = sink.values;
        assertEquals(1, resultedReducedKafkaEvents.size());

    }


    @Test
    public void testValueSplitterCritical() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // configure your test environment
        env.setParallelism(1);
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        CollectSink sink = new CollectSink();
        // values are collected in a static variable
        sink.values.clear();

        // create a stream of custom elements and apply filter transformations
        KafkaEvent testKafkaEvent0 = new KafkaEvent("ID0", 0.0, 10.25, 1541907744041835L,"description");
        KafkaEvent testKafkaEvent20 = new KafkaEvent("ID1", 20.0, 32.25, 1541907744041835L, "description");
        KafkaEvent testKafkaEvent71 = new KafkaEvent("ID71", 71.0, 80.25, 1541907744041835L,"description");
        KafkaEvent testKafkaEvent_80 = new KafkaEvent("ID870", 80.00, 81.25, 1541907744041835L, "description");
        KafkaEvent testKafkaEvent_91 = new KafkaEvent("ID91", 91.0001, 100.25, 1541907744041835L,"description");
        KafkaEvent testKafkaEvent_min1 = new KafkaEvent("ID22", -1.0, 2.25, 1541907744041835L, "description");
        KafkaEvent testKafkaEvent_min20 = new KafkaEvent("ID23", -20.0, 40.25, 1541907744041835L, "description");

        DataStream<KafkaEvent<String, Double, Double>> events = env.fromElements(testKafkaEvent0, testKafkaEvent20, testKafkaEvent_min1,
                testKafkaEvent_min20, testKafkaEvent71, testKafkaEvent_80, testKafkaEvent_91);

        SplitStream<KafkaEvent<String, Double, Double>> splitStream = events.split(new ValueSplitter());

        splitStream.select("critical").addSink(sink);
        List<KafkaEvent> resultedCriticalKafkaEvents = sink.values;

        // execute
        env.execute();

        // verify your results
        assertEquals(4, resultedCriticalKafkaEvents.size());
    }

    @Test
    public void testValueSplitterHigh() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // configure your test environment
        env.setParallelism(1);
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        CollectSink sink = new CollectSink();
        // values are collected in a static variable
        sink.values.clear();

        // create a stream of custom elements and apply filter transformations
        KafkaEvent testKafkaEvent0 = new KafkaEvent("ID0", 0.0, 10.25, 1541907744041835L,"description");
        KafkaEvent testKafkaEvent20 = new KafkaEvent("ID1", 20.0, 32.25, 1541907744041835L, "description");
        KafkaEvent testKafkaEvent71 = new KafkaEvent("ID71", 71.0, 80.25, 1541907744041835L,"description");
        KafkaEvent testKafkaEvent_80 = new KafkaEvent("ID870", 80.00, 81.25, 1541907744041835L, "description");
        KafkaEvent testKafkaEvent_91 = new KafkaEvent("ID91", 91.0001, 100.25, 1541907744041835L,"description");
        KafkaEvent testKafkaEvent_min1 = new KafkaEvent("ID22", -1.0, 2.25, 1541907744041835L, "description");
        KafkaEvent testKafkaEvent_min20 = new KafkaEvent("ID23", -20.0, 40.25, 1541907744041835L, "description");

        DataStream<KafkaEvent<String, Double, Double>> events = env.fromElements(testKafkaEvent0, testKafkaEvent20, testKafkaEvent_min1,
                testKafkaEvent_min20, testKafkaEvent71, testKafkaEvent_80, testKafkaEvent_91);

        SplitStream<KafkaEvent<String, Double, Double>> splitStream = events.split(new ValueSplitter());

        splitStream.select("high").addSink(sink);
        List<KafkaEvent> resultedHighKafkaEvents = sink.values;
        // execute
        env.execute();

        // verify your results
        assertEquals(2, resultedHighKafkaEvents.size());

    }

    @Test
    public void testValueSplitterLow() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // configure your test environment
        env.setParallelism(1);
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        CollectSink sink = new CollectSink();
        // values are collected in a static variable
        sink.values.clear();

        // create a stream of custom elements and apply filter transformations
        KafkaEvent testKafkaEvent0 = new KafkaEvent("ID0", 0.0, 10.25, 1541907744041835L,"description");
        KafkaEvent testKafkaEvent20 = new KafkaEvent("ID1", 20.0, 32.25, 1541907744041835L, "description");
        KafkaEvent testKafkaEvent71 = new KafkaEvent("ID71", 71.0, 80.25, 1541907744041835L,"description");
        KafkaEvent testKafkaEvent_80 = new KafkaEvent("ID870", 80.00, 81.25, 1541907744041835L, "description");
        KafkaEvent testKafkaEvent_91 = new KafkaEvent("ID91", 91.0001, 100.25, 1541907744041835L,"description");
        KafkaEvent testKafkaEvent_min1 = new KafkaEvent("ID22", -1.0, 2.25, 1541907744041835L, "description");
        KafkaEvent testKafkaEvent_min20 = new KafkaEvent("ID23", -20.0, 40.25, 1541907744041835L, "description");

        DataStream<KafkaEvent<String, Double, Double>> events = env.fromElements(testKafkaEvent0, testKafkaEvent20, testKafkaEvent_min1,
                testKafkaEvent_min20, testKafkaEvent71, testKafkaEvent_80, testKafkaEvent_91);

        SplitStream<KafkaEvent<String, Double, Double>> splitStream = events.split(new ValueSplitter());

        splitStream.select("low").addSink(sink);
        List<KafkaEvent> resultedLowKafkaEvents = sink.values;
        // execute
        env.execute();

        // verify your results
        assertEquals(1, resultedLowKafkaEvents.size());

    }


    // create a testing sink
    private static class CollectSink  implements SinkFunction<KafkaEvent<String, Double, Double>> {

        // must be static
        public static final List<KafkaEvent> values = new ArrayList<KafkaEvent>();

        @Override
        public synchronized void invoke(KafkaEvent kafkaEvent) throws Exception {
            values.add(kafkaEvent);
        }
    }

} 
