package functions;

import com.futurewei.everest.datatypes.EverestCollectorData;
import com.futurewei.everest.datatypes.EverestCollectorDataT;
import com.futurewei.everest.functions.CustomWatermarkExtractor;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/** 
* CustomWatermarkExtractor Tester. 
* 
* @author Indra G Harijono
* @since <pre>Nov 11, 2018</pre> 
* @version 1.0 
*/ 
public class CustomWatermarkExtractorTest {
    String cluster_id = "cluster_id";
    private EverestCollectorData testEverestCollectorData;
    private long ts = 0L;
    private List<EverestCollectorData.CpuData> cpuData;
    long expectedRightNow = 0L;
    private List<EverestCollectorDataT<Double>> memData;
    private String containerName = "My Cont Name";
    private String podName = "My Pod Name";
    private String namespace = "My NameSpace";

    @Before
    public void before() throws Exception {
        cpuData = new ArrayList<EverestCollectorData.CpuData>();
        memData = new ArrayList<EverestCollectorDataT<Double>>();
        for(int i=0; i < 3; i++) {
            cpuData.add(new EverestCollectorData.CpuData("myid" + i, 0.1 + i));
            memData.add(new EverestCollectorDataT<Double>(containerName, podName, namespace, 10.1 + i));
        }

    }

    @After
    public void after() throws Exception {
    }

    /**
    *
    * Method: extractTimestamp(EverestCollectorData event, long previousElementTimestamp)
    *
    */
    @Test
    public void testExtractTimestamp() throws Exception {
        testEverestCollectorData = new EverestCollectorData(cluster_id, ts, cpuData, memData);
        CustomWatermarkExtractor customWatermarkExtractor = new CustomWatermarkExtractor();

        assertEquals(expectedRightNow, customWatermarkExtractor.extractTimestamp(testEverestCollectorData, 0L));

    }

    /**
    *
    * Method: getCurrentWatermark()
    *
    */
    @Test
    public void testGetCurrentWatermark() throws Exception {
        testEverestCollectorData = new EverestCollectorData(cluster_id, ts, cpuData, memData);
        CustomWatermarkExtractor customWatermarkExtractor = new CustomWatermarkExtractor();
        long tstamp = customWatermarkExtractor.extractTimestamp(testEverestCollectorData, 0L);

        assertEquals(new Watermark(expectedRightNow - 1500), customWatermarkExtractor.checkAndGetNextWatermark(testEverestCollectorData, tstamp ));
    }


} 
