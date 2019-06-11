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

import com.futurewei.everest.EverestDefaultValues;
import com.futurewei.everest.datatypes.EverestCollectorData;
import com.futurewei.everest.datatypes.EverestCollectorDataT;
import com.futurewei.everest.datatypes.EverestCollectorSerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/** 
* EverestCollectorSerializationSchema Tester. 
* 
* @author Indra G Harijono
* @since <pre>May 27, 2019</pre> 
* @version 1.0 
*/ 
public class EverestCollectorSerializationSchemaTest {

    /**
     * serialization(EverestCollectorData everestCollectorData)
     * testing:
     * Method: deserialize(byte[] message)
     * Method: serialize(EverestCollectorData)
     */
    @Test
    public void testSerialization() throws Exception {
        byte[] serialized;
        List<EverestCollectorDataT<Double, Double>> cpuDatas = new ArrayList<EverestCollectorDataT<Double, Double>>();
        List<EverestCollectorDataT<Double, Double>> memDatas = new ArrayList<EverestCollectorDataT<Double, Double>>();
        List<EverestCollectorDataT<Double, Double>> netDatas = new ArrayList<EverestCollectorDataT<Double, Double>>();
        String containerName = "My Cont Name";
        String podName = "My Pod Name";
        String namespace = "My NameSpace";
        String cluster_id = "my_cluster_id";
        String metric = "My metric";
        String type = EverestDefaultValues.TYPE_TO_COLLECT_MEM;
        long ts = 10L;

        for(int i=0; i < 3; i++) {
            cpuDatas.add(new EverestCollectorDataT<Double, Double>(cluster_id, containerName, podName, namespace, metric + i, type, ts, 1.1 + i, 5.05 + i));
            memDatas.add(new EverestCollectorDataT<Double, Double>(cluster_id, containerName, podName, namespace, metric + i, type, ts, 10.1 + i, 50.5 + i));
        }

        EverestCollectorData testEverestCollectorData = new EverestCollectorData("cluster_id", 0L, cpuDatas, memDatas, netDatas);
        EverestCollectorSerializationSchema everestCollectorSerializationSchema = new EverestCollectorSerializationSchema();
        serialized = everestCollectorSerializationSchema.serialize(testEverestCollectorData);
        EverestCollectorData resultedEverestCollectorData = everestCollectorSerializationSchema.deserialize(serialized);
        assertEquals(testEverestCollectorData.getCluster_id(), resultedEverestCollectorData.getCluster_id());
        assertEquals(testEverestCollectorData.getTs(), resultedEverestCollectorData.getTs());
        List<EverestCollectorDataT<Double, Double>> testCpuDatas = testEverestCollectorData.getCpuData();
        List<EverestCollectorDataT<Double, Double>> resultedCpuDatas = resultedEverestCollectorData.getCpuData();
        for(int i=0; i < testCpuDatas.size(); i++) {
            EverestCollectorDataT<Double, Double> cpuData = testCpuDatas.get(i);
            EverestCollectorDataT<Double, Double> rCpuData = resultedCpuDatas.get(i);
            assertEquals(cpuData.getContainerName(), rCpuData.getContainerName());
            assertEquals(cpuData.getValue(), rCpuData.getValue(), 0.01);
            assertEquals(cpuData.getPercentage(), rCpuData.getPercentage(), 0.01);
            assertEquals(cpuData.getPodName(), rCpuData.getPodName());
            assertEquals(cpuData.getNamespace(), rCpuData.getNamespace());
            assertEquals(cpuData.getType(), rCpuData.getType());
        }
        List<EverestCollectorDataT<Double, Double>> testMemDatas = testEverestCollectorData.getMemData();
        List<EverestCollectorDataT<Double, Double>> resultedMemDatas = resultedEverestCollectorData.getMemData();
        for(int i=0; i < testMemDatas.size(); i++) {
            EverestCollectorDataT<Double, Double> memData = testMemDatas.get(i);
            EverestCollectorDataT<Double, Double> rMemData = resultedMemDatas.get(i);
            assertEquals(memData.getContainerName(), rMemData.getContainerName());
            assertEquals(memData.getValue(), rMemData.getValue(), 0.01);
            assertEquals(memData.getPercentage(), rMemData.getPercentage(), 0.01);
            assertEquals(memData.getPodName(), rMemData.getPodName());
            assertEquals(memData.getNamespace(), rMemData.getNamespace());
            assertEquals(memData.getType(), rMemData.getType());
        }
        List<EverestCollectorDataT<Double, Double>> testNetDatas = testEverestCollectorData.getNetData();
        List<EverestCollectorDataT<Double, Double>> resultedNetDatas = resultedEverestCollectorData.getNetData();
        for(int i=0; i < testNetDatas.size(); i++) {
            EverestCollectorDataT<Double, Double> netData = testNetDatas.get(i);
            EverestCollectorDataT<Double, Double> rNetData = resultedNetDatas.get(i);
            assertEquals(netData.getContainerName(), rNetData.getContainerName());
            assertEquals(netData.getValue(), rNetData.getValue(), 0.01);
            assertEquals(netData.getPercentage(), rNetData.getPercentage(), 0.01);
            assertEquals(netData.getPodName(), rNetData.getPodName());
            assertEquals(netData.getNamespace(), rNetData.getNamespace());
            assertEquals(netData.getType(), rNetData.getType());
        }


    }

    /**
     * Method: isEndOfStream(EverestCollectorData nextElement)
     */
    @Test
    public void testIsEndOfStream() throws Exception {
        // Nothing to test here
    }

    /**
     * Method: getProducedType()
     */
    @Test
    public void testGetProducedType() throws Exception {
        EverestCollectorSerializationSchema everestCollectorSerializationSchema = new EverestCollectorSerializationSchema();
        assertEquals(TypeInformation.of(new TypeHint<EverestCollectorData>() {
        }), everestCollectorSerializationSchema.getProducedType());
    }

}
