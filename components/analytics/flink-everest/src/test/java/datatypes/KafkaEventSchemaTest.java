package datatypes;

import com.futurewei.everest.datatypes.KafkaEvent;
import com.futurewei.everest.datatypes.KafkaEventSchema;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/** 
* KafkaEventSchema Tester. 
* 
* @author Indra G Harijono
* @since <pre>Nov 11, 2018</pre> 
* @version 1.0 
*/ 
public class KafkaEventSchemaTest { 

    /**
    *
    * Method: serialize(KafkaEvent event)
    *
    */
    @Test
    public void testSerialization() throws Exception {
        byte[] serialized;

        KafkaEvent<String, Double, Double> testKafkaEvent = new KafkaEvent("ID0", 20.0, 40.0, 1541907744041835L, "description");
        KafkaEventSchema kafkaEventSchema = new KafkaEventSchema();
        serialized = kafkaEventSchema.serialize(testKafkaEvent);
        KafkaEvent resultedKafkaEvent = kafkaEventSchema.deserialize(serialized);
        assertEquals(testKafkaEvent.getId(), resultedKafkaEvent.getId());
        assertEquals(testKafkaEvent.getValue(), resultedKafkaEvent.getValue());
        assertEquals(testKafkaEvent.getValue0(), resultedKafkaEvent.getValue0());
        assertEquals(testKafkaEvent.getTimestamp(), resultedKafkaEvent.getTimestamp());
        assertEquals(testKafkaEvent.getDescription(), resultedKafkaEvent.getDescription());

    }

    /**
    *
    * Method: isEndOfStream(KafkaEvent nextElement)
    *
    */
    @Test
    public void testIsEndOfStream() throws Exception {
        //Nothing to test, just return false because it is a streaming
    }

    /**
    *
    * Method: getProducedType()
    *
    */
    @Test
    public void testGetProducedType() throws Exception {
        KafkaEventSchema kafkaEventSchema = new KafkaEventSchema();
        assertEquals(TypeInformation.of(new TypeHint<KafkaEvent<String, Double, Double>>(){}), kafkaEventSchema.getProducedType());
    }


} 
