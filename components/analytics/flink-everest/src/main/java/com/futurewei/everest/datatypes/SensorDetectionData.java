/*
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

import java.util.Set;

/**
 * The data type used to keep incoming sensors and compared with the one in the registry in the Eliot IoT Demo.
 *
 * <p>This is a Java POJO, which Flink recognizes and will allow "by-name" field referencing
 * when keying a {@link org.apache.flink.streaming.api.datastream.DataStream} of such a type.
 */

public class SensorDetectionData {
    public Set<String> sensorFromRegistry; // the registered sensors, this will be initialized every interval of time
    public Set<String> matchedSensors; // the sensors that matched with the registered sensors
    public Set<String> unmatchedSensors; // the sensors without a matched with any of the registered sensors
    public int pollInterval; // the frequency of polling the registry in msec
    public long lastModified;
    public boolean timerInitialized;
}
