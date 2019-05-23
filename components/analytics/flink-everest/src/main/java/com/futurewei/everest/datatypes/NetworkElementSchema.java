/*
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

package com.futurewei.everest.datatypes;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.TypeExtractor;

/**
 * Implements a SerializationSchema and DeserializationSchema for NetworkElement for Kafka data sources and sinks.
 */

public class NetworkElementSchema implements DeserializationSchema<NetworkElement>, SerializationSchema<NetworkElement> {

    private static final long serialVersionUID = 2032261696388046393L;

    @Override
        public byte[] serialize(NetworkElement element) {
            return element.toString().getBytes();
        }

        @Override
        public NetworkElement deserialize(byte[] message) {
            return NetworkElement.fromString(new String(message));
        }

        @Override
        public boolean isEndOfStream(NetworkElement nextElement) {
            return false;
        }

        @Override
        public TypeInformation<NetworkElement> getProducedType() {
            return TypeExtractor.getForClass(NetworkElement.class);
    }
}
