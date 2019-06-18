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

import java.util.Map;
import java.util.Objects;


@JsonSerialize
public class EverestCollectorTrace {
    String cluster_id;
    long ts;
    String type;
    String url;
    Map<String, EverestCollectorTraceService> traces;


    /**
     * Constructors
     */
    public EverestCollectorTrace() {}

    public EverestCollectorTrace(String cluster_id, long ts, String type, String url, Map<String, EverestCollectorTraceService> traces) {
        this.cluster_id = cluster_id;
        this.ts = ts;
        this.type = type;
        this.url = url;
        this.traces = traces;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, EverestCollectorTraceService> getTraces() {
        return traces;
    }

    public void setTraces(Map<String, EverestCollectorTraceService> traces) {
        this.traces = traces;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EverestCollectorTrace)) return false;
        EverestCollectorTrace that = (EverestCollectorTrace) o;
        return ts == that.ts &&
                cluster_id.equals(that.cluster_id) &&
                type.equals(that.type) &&
                url.equals(that.url) &&
                traces.equals(that.traces);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cluster_id, ts, type, url, traces);
    }
}
