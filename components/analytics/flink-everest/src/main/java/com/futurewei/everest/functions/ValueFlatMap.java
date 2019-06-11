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


package com.futurewei.everest.functions;

import com.futurewei.everest.EverestDefaultValues;
import com.futurewei.everest.datatypes.EverestCollectorData;
import com.futurewei.everest.datatypes.EverestCollectorDataT;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.dropwizard.metrics.DropwizardMeterWrapper;
import org.apache.flink.metrics.Gauge;
import org.apache.flink.metrics.Meter;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.List;

public class ValueFlatMap extends RichFlatMapFunction<EverestCollectorData, EverestCollectorDataT<Double, Double>> {
    private static final long serialVersionUID = 8273479696640556346L;

    /**
     * Metrics for Prometheus
     */
    private transient Meter cpuThroughput;
    private transient Meter memThroughput;
    private transient Meter netThroughput;
    private transient int clusterNumbers = 0;
    private transient List<String> clusterSeen;
    private transient int podNumbers = 0;
    private transient List<String> podSeen;


    // A type of collection to store everest data. This will be stored in memory
    // of a task manager
    String typeToCollect;

    public ValueFlatMap(String typeToCollect) {
        this.typeToCollect = typeToCollect;
    }

    @Override
    public void open(Configuration config) {
        clusterSeen = new ArrayList<String>();
        getRuntimeContext()
                .getMetricGroup()
                .addGroup(EverestDefaultValues.EVEREST_METRICS_GROUP)
                .gauge(EverestDefaultValues.CLUSTER_NUMBERS, new Gauge<Integer>() {
                    @Override
                    public Integer getValue() {
                        return clusterNumbers;
                    }
                });

        podSeen = new ArrayList<String>();
        getRuntimeContext()
                .getMetricGroup()
                .addGroup(EverestDefaultValues.EVEREST_METRICS_GROUP)
                .gauge(EverestDefaultValues.POD_NUMBERS, new Gauge<Integer>() {
                    @Override
                    public Integer getValue() {
                        return podNumbers;
                    }
                });

        com.codahale.metrics.Meter cpuDropwizardMeter = new com.codahale.metrics.Meter();
        this.cpuThroughput = getRuntimeContext()
                .getMetricGroup()
                .addGroup(EverestDefaultValues.EVEREST_METRICS_GROUP)
                .meter(EverestDefaultValues.CPU_THROUGHPUT, new DropwizardMeterWrapper(cpuDropwizardMeter));
        com.codahale.metrics.Meter memDropwizardMeter = new com.codahale.metrics.Meter();
        this.memThroughput = getRuntimeContext()
                .getMetricGroup()
                .addGroup(EverestDefaultValues.EVEREST_METRICS_GROUP)
                .meter(EverestDefaultValues.MEM_THROUGHPUT, new DropwizardMeterWrapper(memDropwizardMeter));
        com.codahale.metrics.Meter netDropwizardMeter = new com.codahale.metrics.Meter();
        this.netThroughput = getRuntimeContext()
                .getMetricGroup()
                .addGroup(EverestDefaultValues.EVEREST_METRICS_GROUP)
                .meter(EverestDefaultValues.NET_THROUGHPUT, new DropwizardMeterWrapper(memDropwizardMeter));

    }

    @Override
    public void flatMap(EverestCollectorData data, Collector<EverestCollectorDataT<Double, Double>> out) throws Exception {
        if(!clusterSeen.contains(data.getCluster_id())) {
            clusterNumbers++;
            clusterSeen.add(data.getCluster_id());
        }

        List<EverestCollectorDataT<Double, Double>> listDatas;
        String type = EverestDefaultValues.TYPE_TO_COLLECT_CPU;
        if(typeToCollect.equals(EverestDefaultValues.TYPE_TO_COLLECT_CPU)) {
            if(cpuThroughput != null)
                cpuThroughput.markEvent();

            listDatas = data.getCpuData();
        } else if(typeToCollect.equals(EverestDefaultValues.TYPE_TO_COLLECT_MEM)) {
            if(memThroughput != null)
                memThroughput.markEvent();

            listDatas = data.getMemData();
            type = EverestDefaultValues.TYPE_TO_COLLECT_MEM;
        } else if(typeToCollect.equals(EverestDefaultValues.TYPE_TO_COLLECT_NET)) {
            if(netThroughput != null)
                netThroughput.markEvent();

            listDatas = data.getNetData();
            type = EverestDefaultValues.TYPE_TO_COLLECT_NET;
        } else {
            System.out.println("***** ERROR *****: unexpected type to filter in RichFlatMapFunction ValueFlatMap Class");
            throw (new Exception("unexpected type to filter in RichFlatMapFunction ValueFlatMap Class"));
        }
        for(EverestCollectorDataT<Double, Double> lData: listDatas) {
            if(!podSeen.contains(lData.getContainerName())) {
                podNumbers++;
                podSeen.add(lData.getPodName());
            }
            lData.setCluster_id(data.getCluster_id());
            lData.setType(type);
            out.collect(lData);
        }
    }

}
