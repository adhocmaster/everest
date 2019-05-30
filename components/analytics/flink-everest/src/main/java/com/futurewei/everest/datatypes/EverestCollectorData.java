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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;
import java.util.Map;
import java.util.Objects;

//import java.time.LocalDateTime;
//import java.time.ZoneId;

@JsonSerialize
public class EverestCollectorData {
    // EverestCollectorDataT<Double> cpu;
    // EverestCollectorDataT<Double> mem;
    // EverestCollectorDataT<Integer> ts;

    String cluster_id;
    long ts;
    List<CpuData> cpuData;
    List<EverestCollectorDataT<Double>> memData;

    /**
     * Inner Class
     */

    @JsonSerialize
    public static class CpuData {
        private String id;
        private double value;

        public CpuData() {
        }

        public CpuData(String id, double value) {
            this.id = id;
            this.value = value;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }
    }

    /**
     * Constructors
     */
    public EverestCollectorData() {}

    public EverestCollectorData(String cluster_id, long ts, List<CpuData> cpuData, List<EverestCollectorDataT<Double>> memData) {
        this.cluster_id = cluster_id;
        this.ts = ts;
        this.cpuData = cpuData;
        this.memData = memData;
    }

    /**
     * Getters and Setters
     *
     */
    public String getCluster_id() {
        return cluster_id;
    }

    public void setCluster_id(String cluster_id) {
        this.cluster_id = cluster_id;
    }

    public long getTs() {
        return ts;
    }
    public void setTs(long ts) {
        this.ts = ts;
    }

    public List<CpuData> getCpuData() {
        return cpuData;
    }

    public void setCpuData(List<CpuData> cpuData) {
        this.cpuData = cpuData;
    }

    public long localToTimeStamp() {
//        ZoneId zoneId = ZoneId.systemDefault();
//        return this.getTs().atZone(zoneId).toEpochSecond() * 1000;
        return ts;
    }

    public List<EverestCollectorDataT<Double>> getMemData() {
        return memData;
    }

    public void setMemData(List<EverestCollectorDataT<Double>> memData) {
        this.memData = memData;
    }

    /**
     * jackson related fields
     */

//    @SuppressWarnings("unchecked")
//    @JsonProperty("cpu")
//    private void unpackCpu(Map<String,Object> cpu) {
//        this.brandName = (String)brand.get("name");
//        Map<String,String> owner = (Map<String,String>)brand.get("owner");
//        this.ownerName = owner.get("name");
//    }
//


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EverestCollectorData)) return false;
        EverestCollectorData that = (EverestCollectorData) o;
        return ts == that.ts &&
                cluster_id.equals(that.cluster_id) &&
                cpuData.equals(that.cpuData) &&
                memData.equals(that.memData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cluster_id, ts, cpuData, memData);
    }
}
