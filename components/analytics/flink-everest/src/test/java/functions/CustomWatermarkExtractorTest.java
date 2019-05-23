package functions;

import com.futurewei.everest.datatypes.KafkaEvent;
import com.futurewei.everest.functions.CustomWatermarkExtractor;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/** 
* CustomWatermarkExtractor Tester. 
* 
* @author Indra G Harijono
* @since <pre>Nov 11, 2018</pre> 
* @version 1.0 
*/ 
public class CustomWatermarkExtractorTest { 

    /**
    *
    * Method: extractTimestamp(KafkaEvent event, long previousElementTimestamp)
    *
    */
    @Test
    public void testExtractTimestamp() throws Exception {
        KafkaEvent testKafkaEvent = new KafkaEvent("ID0", 20.0, 40.0, 1541907744041835L, "description");
        CustomWatermarkExtractor customWatermarkExtractor = new CustomWatermarkExtractor();

        assertEquals(1541907744041835L, customWatermarkExtractor.extractTimestamp(testKafkaEvent, 0L));

    }

    /**
    *
    * Method: getCurrentWatermark()
    *
    */
    @Test
    public void testGetCurrentWatermark() throws Exception {
        KafkaEvent testKafkaEvent = new KafkaEvent("ID0", 20.0, 40.0, 1541907744041835L, "description");
        CustomWatermarkExtractor customWatermarkExtractor = new CustomWatermarkExtractor();
        customWatermarkExtractor.extractTimestamp(testKafkaEvent, 0L);

        assertEquals(new Watermark(1541907744041835L - 1L), customWatermarkExtractor.getCurrentWatermark());

    }


} 
