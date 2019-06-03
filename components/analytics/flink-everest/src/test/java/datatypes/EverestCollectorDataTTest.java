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

import com.futurewei.everest.datatypes.EverestCollectorDataT;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;


import static org.junit.Assert.*;

/** 
* EverestCollectorDataT Tester. 
* 
* @author Indra G Harijono
* @since <pre>May 31, 2019</pre> 
* @version 1.0 
*/ 
public class EverestCollectorDataTTest {
    String cluster_id = "my_cluster_id";
    private long ts = 0L;
    private EverestCollectorDataT<Double, Double> cpuData;
    private EverestCollectorDataT<Double, Double> memData;
    private EverestCollectorDataT<Double, Double> netData;
    private String containerName = "My Cont Name";
    private String podName = "My Pod Name";
    private String namespace = "My NameSpace";

    @Before
    public void before() throws Exception {
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
        cpuData = new EverestCollectorDataT<Double, Double>(cluster_id, containerName, podName, namespace, ts, 1.0, 2.0);
        assertEquals(cluster_id, cpuData.getCluster_id());
    }

    /**
    *
    * Method: setCluster_id(String cluster_id)
    *
    */
    @Test
    public void testSetCluster_id() throws Exception {
        cpuData = new EverestCollectorDataT<Double, Double>(cluster_id, containerName, podName, namespace, ts, 1.0, 2.0);
        assertEquals(cluster_id, cpuData.getCluster_id());
        cpuData.setCluster_id(cluster_id + cluster_id);
        assertEquals(cluster_id+cluster_id, cpuData.getCluster_id());
    }

    /**
    *
    * Method: getTs()
    *
    */
    @Test
    public void testGetTs() throws Exception {
        cpuData = new EverestCollectorDataT<Double, Double>(cluster_id, containerName, podName, namespace, ts, 1.0, 2.0);
        assertEquals(ts, cpuData.getTs());

    }

    /**
    *
    * Method: setTs(long ts)
    *
    */
    @Test
    public void testSetTs() throws Exception {
        cpuData = new EverestCollectorDataT<Double, Double>(cluster_id, containerName, podName, namespace, ts, 1.0, 2.0);
        assertEquals(ts, cpuData.getTs());
        cpuData.setTs(ts + ts);
        assertEquals(ts+ts, cpuData.getTs());
    }

    /**
    *
    * Method: getContainerName()
    *
    */
    @Test
    public void testGetContainerName() throws Exception {
        cpuData = new EverestCollectorDataT<Double, Double>(cluster_id, containerName, podName, namespace, ts, 1.0, 2.0);
        assertEquals(containerName, cpuData.getContainerName());

    }

    /**
    *
    * Method: setContainerName(String containerName)
    *
    */
    @Test
    public void testSetContainerName() throws Exception {
        cpuData = new EverestCollectorDataT<Double, Double>(cluster_id, containerName, podName, namespace, ts, 1.0, 2.0);
        assertEquals(containerName, cpuData.getContainerName());
        cpuData.setContainerName(containerName+containerName);
        assertEquals(containerName+containerName, cpuData.getContainerName());
    }

    /**
    *
    * Method: getPodName()
    *
    */
    @Test
    public void testGetPodName() throws Exception {
        cpuData = new EverestCollectorDataT<Double, Double>(cluster_id, containerName, podName, namespace, ts, 1.0, 2.0);
        assertEquals(podName, cpuData.getPodName());
    }

    /**
    *
    * Method: setPodName(String podName)
    *
    */
    @Test
    public void testSetPodName() throws Exception {
        cpuData = new EverestCollectorDataT<Double, Double>(cluster_id, containerName, podName, namespace, ts, 1.0, 2.0);
        assertEquals(podName, cpuData.getPodName());
        cpuData.setPodName(podName+podName);
        assertEquals(podName+podName, cpuData.getPodName());
    }

    /**
    *
    * Method: getNamespace()
    *
    */
    @Test
    public void testGetNamespace() throws Exception {
        cpuData = new EverestCollectorDataT<Double, Double>(cluster_id, containerName, podName, namespace, ts, 1.0, 2.0);
        assertEquals(namespace, cpuData.getNamespace());

    }

    /**
    *
    * Method: setNamespace(String namespace)
    *
    */
    @Test
    public void testSetNamespace() throws Exception {
        cpuData = new EverestCollectorDataT<Double, Double>(cluster_id, containerName, podName, namespace, ts, 1.0, 2.0);
        assertEquals(namespace, cpuData.getNamespace());
        cpuData.setNamespace(namespace+namespace);
        assertEquals(namespace+namespace, cpuData.getNamespace());

    }

    /**
    *
    * Method: getValue()
    *
    */
    @Test
    public void testGetValue() throws Exception {
        cpuData = new EverestCollectorDataT<Double, Double>(cluster_id, containerName, podName, namespace, ts, 1.0, 2.0);
        assertEquals(1.0, cpuData.getValue(), 0.01);

    }

    /**
    *
    * Method: setValue(V value)
    *
    */
    @Test
    public void testSetValue() throws Exception {
        cpuData = new EverestCollectorDataT<Double, Double>(cluster_id, containerName, podName, namespace, ts, 1.0, 2.0);
        assertEquals(1.0, cpuData.getValue(), 0.01);
        cpuData.setValue(10.0);
        assertEquals(10.0, cpuData.getValue(), 0.01);
    }

    /**
    *
    * Method: getPercentage()
    *
    */
    @Test
    public void testGetPercentage() throws Exception {
        cpuData = new EverestCollectorDataT<Double, Double>(cluster_id, containerName, podName, namespace, ts, 1.0, 2.0);
        assertEquals(2.0, cpuData.getPercentage(), 0.01);

    }

    /**
    *
    * Method: setPercentage(W percentage)
    *
    */
    @Test
    public void testSetPercentage() throws Exception {
        cpuData = new EverestCollectorDataT<Double, Double>(cluster_id, containerName, podName, namespace, ts, 1.0, 2.0);
        assertEquals(2.0, cpuData.getPercentage(), 0.01);
        cpuData.setPercentage(20.0);
        assertEquals(20.0, cpuData.getPercentage(), 0.01);
    }

    /**
    *
    * Method: equals(Object o)
    *
    */
    @Test
    public void testEquals() throws Exception {
        cpuData = new EverestCollectorDataT<Double, Double>(cluster_id, containerName, podName, namespace, ts, 1.0, 2.0);
        memData = new EverestCollectorDataT<Double, Double>(cluster_id, containerName, podName, namespace, ts, 1.0, 2.0);
        assertEquals(cpuData, cpuData);
        assertEquals(memData, memData);
        assertEquals(cpuData, memData);
        memData = new EverestCollectorDataT<Double, Double>(cluster_id, containerName, podName, namespace, ts, 10.0, 2.0);
        assertNotEquals(cpuData, memData);
    }

    /**
    *
    * Method: hashCode()
    *
    */
    @Test
    public void testHashCode() throws Exception {
        cpuData = new EverestCollectorDataT<Double, Double>(cluster_id, containerName, podName, namespace, ts, 1.0, 2.0);
        memData = new EverestCollectorDataT<Double, Double>(cluster_id, containerName, podName, namespace, ts, 1.0, 2.0);
        assertEquals(cpuData.hashCode(), memData.hashCode());
        assertEquals(cpuData.hashCode(), cpuData.hashCode());
        assertEquals(memData.hashCode(), memData.hashCode());
        memData = new EverestCollectorDataT<Double, Double>(cluster_id, containerName, podName, namespace, ts, 10.0, 2.0);
        assertNotEquals(cpuData.hashCode(), memData.hashCode());

    }


} 
