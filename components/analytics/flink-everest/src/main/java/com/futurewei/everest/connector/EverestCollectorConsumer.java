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

package com.futurewei.everest.connector;

import com.futurewei.everest.datatypes.EverestCollectorSerializationSchema;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import com.futurewei.everest.datatypes.EverestCollectorData;
import org.apache.flink.streaming.connectors.kafka.config.StartupMode;

import java.util.Properties;

public class EverestCollectorConsumer {
    public static FlinkKafkaConsumer010<EverestCollectorData> createEverestCollectorDataConsumer(String topic, Properties properties, StartupMode startupMode) {
        FlinkKafkaConsumer010<EverestCollectorData> consumer = new FlinkKafkaConsumer010<EverestCollectorData>(
                topic, new EverestCollectorSerializationSchema(), properties);

        switch(startupMode) {
            case EARLIEST:
                consumer.setStartFromEarliest();
                break;
            case LATEST:
                consumer.setStartFromLatest();
                break;
//            case SPECIFIC_OFFSETS:
//                consumer.setStartFromSpecificOffsets(specificStartupOffsets);
//                break;
            case GROUP_OFFSETS:
                consumer.setStartFromGroupOffsets();
                break;
        }
        return consumer;
    }
}

