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


/**
 * The event type used in the Eliot IoT Demo.
 *
 * <p>This is a Java POJO, which Flink recognizes and will allow "by-name" field referencing
 * when keying a {@link org.apache.flink.streaming.api.datastream.DataStream} of such a type.
 */
public class EnrichedEverestData<I, V, V0, D, A> {
    private I id;
    private V value;
    private V0 value0;
    private D description;
    private A action;

    public EnrichedEverestData() {} // this is a requirement for Flink POJO

    public EnrichedEverestData(I id, V value, V0 value0, D description, A action) {
        this.id = id;
        this.value = value;
        this.value0 = value0;
        this.description = description;
        this.action = action;
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

    public D getDescription() {
        return description;
    }

    public void setDescription(D description) {
        this.description = description;
    }

    public A getAction() {
        return action;
    }

    public void setAction(A action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return id + "," + value + "," + value0 + "," + description + "," + "," + action;
    }
}
