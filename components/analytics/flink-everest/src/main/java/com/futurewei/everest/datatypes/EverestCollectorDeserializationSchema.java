/*
 * Copyright 2018-2019 The Everest Authors
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
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

package com.futurewei.everest.datatypes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class EverestCollectorDeserializationSchema implements DeserializationSchema<EverestCollectorData> , SerializationSchema<EverestCollectorData> {
    private static final long serialVersionUID = 6154188370181669759L;
    static ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    transient Logger logger = LoggerFactory.getLogger(EverestCollectorDeserializationSchema.class);

    @Override
    public EverestCollectorData deserialize(byte[] message) throws IOException {
        EverestCollectorData data = objectMapper.readValue(message, EverestCollectorData.class);
//        System.out.println("Deserialize Data Cluster ID" + data.getCluster_id());
//        System.out.println("Deserialize Data TS" + data.getTs());

//        List<EverestCollectorDataT<Double, Double>> cpuDatas = data.getCpuData();
//        System.out.println("Deserialize Data CPU LEN" + cpuDatas.size());
//        for(EverestCollectorDataT<Double, Double> cpuData : cpuDatas) {
//            System.out.println("\nDeserialize Data MEM CNAME -> " + cpuData.getContainerName());
//            System.out.println("\nDeserialize Data MEM PNAME -> " + cpuData.getPodName());
//            System.out.println("\nDeserialize Data MEM NameSpace -> " + cpuData.getNamespace());
//            System.out.println("\nDeserialize Data MEM VALUE -> " + cpuData.getValue());
//            System.out.println("\nDeserialize Data MEM PERCENT -> " + cpuData.getPercentage());
//        }
//        List<EverestCollectorDataT<Double, Double>> memDatas = data.getMemData();
//        System.out.println("Deserialize Data MEM LEN" + memDatas.size());
//        for(EverestCollectorDataT<Double, Double> memData : memDatas) {
//            System.out.println("\nDeserialize Data MEM CNAME -> " + memData.getContainerName());
//            System.out.println("\nDeserialize Data MEM PNAME -> " + memData.getPodName());
//            System.out.println("\nDeserialize Data MEM NameSpace -> " + memData.getNamespace());
//            System.out.println("\nDeserialize Data MEM VALUE -> " + memData.getValue());
//            System.out.println("\nDeserialize Data MEM PERCENT -> " + memData.getPercentage());
//        }

        return data;
    }
    @Override
    public byte[] serialize(EverestCollectorData everestCollectorData) {
        try {
            return objectMapper.writeValueAsString(everestCollectorData).getBytes();
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            logger.error("Failed to parse JSON", e);
        }
        return new byte[0];
    }

    @Override
    public boolean isEndOfStream(EverestCollectorData nextElement) {
        return false;
    }

    @Override
    public TypeInformation<EverestCollectorData> getProducedType() {
        return TypeInformation.of(new TypeHint<EverestCollectorData>() {
        });
    }
}