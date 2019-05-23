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


import org.jetbrains.annotations.NotNull;

/**
 * The event type used in the Eliot IoT Demo.
 *
 * <p>This is a Java POJO, which Flink recognizes and will allow "by-name" field referencing
 * when keying a {@link org.apache.flink.streaming.api.datastream.DataStream} of such a type.
 */
public class KafkaEvent<I, V, V0> {
    private I id;
    private V value;
    private V0 value0;
    private long timestamp;
    private String description;

    public KafkaEvent() {} // this is a requirement for Flink POJO

    public KafkaEvent(I id, V value, V0 value0, long timestamp, String description) {
        this.id = id;
        this.value = value;
        this.value0 = value0;
        this.timestamp = timestamp;
        this.description = description;
    }

    public I getId() {
        return id;
    }

    public void setId(I id) {
        this.id = id;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public V0 getValue0() {
        return value0;
    }

    public void setValue0(V0 value) {
        this.value0 = value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @NotNull
    public static KafkaEvent<String, Double, Double> fromString(String eventStr) {
        String[] split = eventStr.split(",");
        //System.out.println("ID=" + split[0] + ", Value=" + split[1] + ", Value0=" + split[2] + ", TS=" + split[3] +
        //      ", Description=" + split[4]);
        return new KafkaEvent<>(split[0], Double.valueOf(split[1]), Double.valueOf(split[2]), Long.valueOf(split[3]),
                split[4]);
    }

    @Override
    public String toString() {
        return id + "," + value + "," + value0 + "," + timestamp + "," + "," + description;
    }
}
