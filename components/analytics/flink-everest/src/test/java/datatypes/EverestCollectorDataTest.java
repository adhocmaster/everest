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

package datatypes;

import com.futurewei.everest.datatypes.EverestCollectorData;
import com.futurewei.everest.datatypes.EverestCollectorDataT;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/** 
* EverestCollectorData Tester. 
* 
* @author Indra G Harijono
* @since <pre>May 27, 2019</pre> 
* @version 1.0 
*/ 
public class EverestCollectorDataTest {
    String cluster_id = "my_cluster_id";
    private EverestCollectorData everestCollectorData;
    private long ts = 0L;
    private List<EverestCollectorDataT<Double, Double>> cpuData;
    private List<EverestCollectorDataT<Double, Double>> expectedCpuData;
    private List<EverestCollectorDataT<Double, Double>> memData;
    private List<EverestCollectorDataT<Double, Double>> expectedMemData;
    private String containerName = "My Cont Name";
    private String podName = "My Pod Name";
    private String namespace = "My NameSpace";

    @Before
    public void before() throws Exception {
        cpuData = new ArrayList<EverestCollectorDataT<Double, Double>>();
        memData = new ArrayList<EverestCollectorDataT<Double, Double>>();
        for(int i=0; i < 3; i++) {
            cpuData.add(new EverestCollectorDataT<Double, Double>(cluster_id, containerName, podName, namespace, ts, 5000.1 + i, 50.1 + i));
            memData.add(new EverestCollectorDataT<Double, Double>(cluster_id, containerName, podName, namespace, ts, 1000.1 + i, 10.1 + i));
        }
    }

    @After
    public void after() throws Exception {
    }

    /**
     *
     * Method: getCluster_id()
     *
     */
    @Test
    public void testGetCluster_id() throws Exception {
        everestCollectorData = new EverestCollectorData(cluster_id, ts, cpuData, memData);
        assertEquals(cluster_id, everestCollectorData.getCluster_id());
    }

    /**
     *
     * Method: setCluster_id(String cluster_id)
     *
     */
    @Test
    public void testSetCluster_id() throws Exception {
        everestCollectorData = new EverestCollectorData(cluster_id, ts, cpuData, memData);
        assertEquals(cluster_id, everestCollectorData.getCluster_id());
        everestCollectorData.setCluster_id(cluster_id + cluster_id);
        assertEquals(cluster_id + cluster_id, everestCollectorData.getCluster_id());
    }

    /**
     *
     * Method: getTs()
     *
     */
    @Test
    public void testGetTs() throws Exception {
        everestCollectorData = new EverestCollectorData(cluster_id, ts, cpuData, memData);
        assertEquals(ts, everestCollectorData.getTs());
    }

    /**
     *
     * Method: setTs(long ts)
     *
     */
    @Test
    public void testSetTs() throws Exception {
        everestCollectorData = new EverestCollectorData(cluster_id, ts, cpuData, memData);
        assertEquals(ts, everestCollectorData.getTs());
        everestCollectorData.setTs(ts + ts);
        assertEquals(ts + ts, everestCollectorData.getTs());
    }

    /**
     *
     * Method: getCpuData()
     *
     */
    @Test
    public void testGetCpuData() throws Exception {
        everestCollectorData = new EverestCollectorData(cluster_id, ts, cpuData, memData);
        expectedCpuData = everestCollectorData.getCpuData();
        assertEquals(cpuData, expectedCpuData);
    }

    /**
     *
     * Method: setCpuData(List<EverestCollectorDataT<Double, Double>> cpuData)
     *
     */
    @Test
    public void testSetCpuData() throws Exception {
        everestCollectorData = new EverestCollectorData(cluster_id, ts, cpuData, memData);
        expectedCpuData = everestCollectorData.getCpuData();
        assertEquals(cpuData, expectedCpuData);
        cpuData.remove(0);
        everestCollectorData.setCpuData(cpuData);
        expectedCpuData = everestCollectorData.getCpuData();
        assertEquals(cpuData, expectedCpuData);
    }

    /**
     *
     * Method: localToTimeStamp()
     *
     */
    @Test
    public void testLocalToTimeStamp() throws Exception {
        everestCollectorData = new EverestCollectorData(cluster_id, ts, cpuData, memData);
        long resTs = everestCollectorData.localToTimeStamp();
        assertEquals(resTs, ts);
    }

    /**
     *
     * Method: getMemData()
     *
     */
    @Test
    public void testGetMemData() throws Exception {
        everestCollectorData = new EverestCollectorData(cluster_id, ts, cpuData, memData);
        expectedMemData = everestCollectorData.getMemData();
        assertEquals(memData, expectedMemData);

    }

    /**
     *
     * Method: setMemData(List<EverestCollectorDataT<Double, Double>> memData)
     *
     */
    @Test
    public void testSetMemData() throws Exception {
        everestCollectorData = new EverestCollectorData(cluster_id, ts, cpuData, memData);
        expectedMemData = everestCollectorData.getMemData();
        assertEquals(memData, expectedMemData);
        memData.remove(0);
        everestCollectorData.setCpuData(memData);
        expectedMemData = everestCollectorData.getMemData();
        assertEquals(memData, expectedMemData);
    }

    /**
     *
     * Method: equals(Object o)
     *
     */
    @Test
    public void testEquals() throws Exception {
        everestCollectorData = new EverestCollectorData(cluster_id, ts, cpuData, memData);
        EverestCollectorData expectedEverestCollectorData = new EverestCollectorData(cluster_id, ts, cpuData, memData);
        assertTrue(everestCollectorData.equals(expectedEverestCollectorData));
    }

    /**
     *
     * Method: hashCode()
     *
     */
    @Test
    public void testHashCode() throws Exception {
        everestCollectorData = new EverestCollectorData(cluster_id, ts, cpuData, memData);
        assertTrue(everestCollectorData == everestCollectorData);
    }







} 
