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
import java.util.Objects;

//import java.time.LocalDateTime;
//import java.time.ZoneId;

@JsonSerialize
public class EverestCollectorData {
    String cluster_id;
    long ts;
    List<EverestCollectorDataT<Double, Double>>  cpuData;
    List<EverestCollectorDataT<Double, Double>> memData;

    /**
     * Constructors
     */
    public EverestCollectorData() {}

    public EverestCollectorData(String cluster_id, long ts, List<EverestCollectorDataT<Double, Double>> cpuData, List<EverestCollectorDataT<Double, Double>> memData) {
        this.cluster_id = cluster_id;
        this.ts = ts;
        this.cpuData = cpuData;
        this.memData = memData;
    }

    /**
     * Getters and Setters
     *
     */

    /**
     *
     * @return String
     */
    public String getCluster_id() {
        return cluster_id;
    }

    /**
     *
     * @param cluster_id the id of the cluster
     */
    public void setCluster_id(String cluster_id) {
        this.cluster_id = cluster_id;
    }

    /**
     *
     * @return long
     */
    public long getTs() {
        return ts;
    }

    /**
     *
     * @param ts the timestamp
     */
    public void setTs(long ts) {
        this.ts = ts;
    }

    /**
     *
     * @return List
     */
    public List<EverestCollectorDataT<Double, Double>> getCpuData() {
        return cpuData;
    }

    /**
     *
     * @param cpuData the list of the cpu data
     */
    public void setCpuData(List<EverestCollectorDataT<Double, Double>> cpuData) {
        this.cpuData = cpuData;
    }

    /**
     *
     * @return long
     */
    public long localToTimeStamp() {
        return ts;
    }

    /**
     *
     * @return List
     */
    public List<EverestCollectorDataT<Double, Double>> getMemData() {
        return memData;
    }

    /**
     *
     * @param memData the list of mem data
     */
    public void setMemData(List<EverestCollectorDataT<Double, Double>> memData) {
        this.memData = memData;
    }



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
