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

package functions;

import com.futurewei.everest.datatypes.EverestCollectorDataT;
import com.futurewei.everest.functions.CustomWatermarkExtractor;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
    private EverestCollectorDataT<Double, Double> testEverestCollectorDataT;
    private long ts = 0L;
    private List<EverestCollectorDataT<Double, Double>> cpuData;
    long expectedRightNow = 0L;
    private List<EverestCollectorDataT<Double, Double>> memData;
    private String containerName = "My Cont Name";
    private String podName = "My Pod Name";
    private String namespace = "My NameSpace";

    @Before
    public void before() throws Exception {
        cpuData = new ArrayList<EverestCollectorDataT<Double, Double>>();
        memData = new ArrayList<EverestCollectorDataT<Double, Double>>();
        for(int i=0; i < 3; i++) {
            cpuData.add(new EverestCollectorDataT<Double, Double>(cluster_id, containerName, podName, namespace, ts, 1.1 + i, 5.01 + i));
            memData.add(new EverestCollectorDataT<Double, Double>(cluster_id, containerName, podName, namespace, ts, 10.1 + i, 55.01 + i));
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
        testEverestCollectorDataT = new EverestCollectorDataT<Double, Double>(cluster_id, containerName, podName, namespace, ts, 1.0, 10.0);
        CustomWatermarkExtractor customWatermarkExtractor = new CustomWatermarkExtractor();

        assertEquals(expectedRightNow, customWatermarkExtractor.extractTimestamp(testEverestCollectorDataT, ts));

    }

    /**
    *
    * Method: getCurrentWatermark()
    *
    */
    @Test
    public void testGetCurrentWatermark() throws Exception {
        testEverestCollectorDataT = new EverestCollectorDataT<Double, Double>(cluster_id, containerName, podName, namespace, ts, 1.0, 10.0);
        CustomWatermarkExtractor customWatermarkExtractor = new CustomWatermarkExtractor();
        long tstamp = customWatermarkExtractor.extractTimestamp(testEverestCollectorDataT, 0L);

        assertEquals(new Watermark(expectedRightNow - 1500), customWatermarkExtractor.checkAndGetNextWatermark(testEverestCollectorDataT, tstamp ));
    }


} 
