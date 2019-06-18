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
import java.util.Map;


public class EverestCollectorTraceDeserializationSchema implements DeserializationSchema<EverestCollectorTrace> , SerializationSchema<EverestCollectorTrace> {
    private static final long serialVersionUID = 6154298370181669759L;
    static ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    transient Logger logger = LoggerFactory.getLogger(EverestCollectorTraceDeserializationSchema.class);

    @Override
    public EverestCollectorTrace deserialize(byte[] message) throws IOException {
        EverestCollectorTrace data = objectMapper.readValue(message, EverestCollectorTrace.class);
        System.out.println("Deserialize Trace Cluster ID" + data.getCluster_id());
        System.out.println("Deserialize Trace TS" + data.getTs());
        System.out.println("Deserialize Trace type" + data.getType());
        System.out.println("Deserialize Trace URL" + data.getUrl());

        Map<String, EverestCollectorTraceService> traces = data.getTraces();

        for (Map.Entry<String,EverestCollectorTraceService> entry : traces.entrySet())
            System.out.println("Deserialize TraceService Key = " + entry.getKey() +
                    ", Value = " + entry.getValue());

        return data;
    }
    @Override
    public byte[] serialize(EverestCollectorTrace everestCollectorTrace) {
        try {
            return objectMapper.writeValueAsString(everestCollectorTrace).getBytes();
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            logger.error("Failed to parse JSON", e);
        }
        return new byte[0];
    }

    @Override
    public boolean isEndOfStream(EverestCollectorTrace nextElement) {
        return false;
    }

    @Override
    public TypeInformation<EverestCollectorTrace> getProducedType() {
        return TypeInformation.of(new TypeHint<EverestCollectorTrace>() {
        });
    }
}