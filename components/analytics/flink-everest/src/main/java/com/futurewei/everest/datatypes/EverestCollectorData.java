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

package com.futurewei.everest.datatypes;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Objects;

import java.time.LocalDateTime;

@JsonSerialize
public class EverestCollectorData {
    EverestCollectorDataT<Double> cpu;
    EverestCollectorDataT<Double> mem;
    EverestCollectorDataT<Integer> net;


    public EverestCollectorData() {}

    public EverestCollectorData(EverestCollectorDataT<Double> cpu,
                                EverestCollectorDataT<Double> mem,
                                EverestCollectorDataT<Integer> net) {
        this.cpu = cpu;
        this.mem = mem;
        this.net = net;
    }

    public EverestCollectorDataT<Double> getCpu() {
        return cpu;
    }
    public void setCpu(EverestCollectorDataT<Double> cpu) {
        this.cpu = cpu;
    }
    public EverestCollectorDataT<Double> getMem() {
        return mem;
    }
    public void setMem(EverestCollectorDataT<Double> mem) {
        this.mem = mem;
    }
    public EverestCollectorDataT<Integer> getNet() {
        return net;
    }
    public void setNet(EverestCollectorDataT<Integer> net) {
        this.net = net;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EverestCollectorData data1 = (EverestCollectorData) o;
        return Objects.equal(cpu, data1.cpu) &&
                Objects.equal(mem, data1.mem) &&
                Objects.equal(net, data1.mem);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(cpu, mem, net);
    }

}
