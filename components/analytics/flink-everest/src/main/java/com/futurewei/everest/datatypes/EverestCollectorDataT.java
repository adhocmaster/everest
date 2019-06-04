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

import java.util.Objects;

@JsonSerialize
public class EverestCollectorDataT<V, W> {
    String cluster_id;
    String containerName;
    String podName;
    String namespace;
    String metric;
    long ts;
    V value;
    W percentage;

    public EverestCollectorDataT() {} // this is a requirement for Flink POJO

    public EverestCollectorDataT(String cluster_id, String containerName, String podName, String namespace, String metric, long ts, V value, W percentage) {
        this.cluster_id = cluster_id;
        this.containerName = containerName;
        this.podName = podName;
        this.namespace = namespace;
        this.metric = metric;
        this.ts = ts;
        this.value = value;
        this.percentage = percentage;
    }

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

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    public String getPodName() {
        return podName;
    }

    public void setPodName(String podName) {
        this.podName = podName;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public W getPercentage() {
        return percentage;
    }

    public void setPercentage(W percentage) {
        this.percentage = percentage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EverestCollectorDataT)) return false;
        EverestCollectorDataT<?, ?> that = (EverestCollectorDataT<?, ?>) o;
        return ts == that.ts &&
                cluster_id.equals(that.cluster_id) &&
                containerName.equals(that.containerName) &&
                podName.equals(that.podName) &&
                namespace.equals(that.namespace) &&
                metric.equals(that.metric) &&
                value.equals(that.value) &&
                percentage.equals(that.percentage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cluster_id, containerName, podName, namespace, metric, ts, value, percentage);
    }
}
