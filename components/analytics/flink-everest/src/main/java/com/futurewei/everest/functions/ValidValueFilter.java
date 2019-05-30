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
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.RichFilterFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.dropwizard.metrics.DropwizardMeterWrapper;
import org.apache.flink.metrics.Counter;
import org.apache.flink.metrics.Gauge;
import org.apache.flink.metrics.Meter;

import java.util.ArrayList;
import java.util.List;

public class ValidValueFilter extends RichFilterFunction<EverestCollectorData> {
    /**
     * A {@link FilterFunction} that continuously filter bad values (lower than lower bound or higher than higher bound).
     */
    private static final long serialVersionUID = 8273479696640156346L;
    private transient Counter validCpuCounter;
    private transient Counter invalidCpuCounter;
    private transient Counter validMemCounter;
    private transient Counter invalidMemCounter;
    private transient Meter dataThroughput;
    private transient int clusterNumbers = 0;
    private transient List<String> clusterSeen;
    private transient int criticalCpuNumbers = 0;
    private transient int criticalMemNumbers = 0;

    @Override
    public void open(Configuration config) {
        this.validCpuCounter = getRuntimeContext()
                .getMetricGroup()
                .addGroup("EverestMetrics")
                .counter("valid_cpu_counter");
        this.invalidCpuCounter = getRuntimeContext()
                .getMetricGroup()
                .addGroup("EverestMetrics")
                .counter("invalid_cpu_counter");
        this.validMemCounter = getRuntimeContext()
                .getMetricGroup()
                .addGroup("EverestMetrics")
                .counter("valid_mem_counter");
        this.invalidMemCounter = getRuntimeContext()
                .getMetricGroup()
                .addGroup("EverestMetrics")
                .counter("invalid_mem_counter");
        getRuntimeContext()
                .getMetricGroup()
                .addGroup("EverestMetrics")
                .gauge("cluster_numbers", new Gauge<Integer>() {
                    @Override
                    public Integer getValue() {
                        return clusterNumbers;
                    }
                });

        getRuntimeContext()
                .getMetricGroup()
                .addGroup("EverestMetrics")
                .gauge("critical_mem_number", new Gauge<Integer>() {
                    @Override
                    public Integer getValue() {
                        return criticalCpuNumbers;
                    }
                });
        getRuntimeContext()
                .getMetricGroup()
                .addGroup("EverestMetrics")
                .gauge("critical_mem_number", new Gauge<Integer>() {
                    @Override
                    public Integer getValue() {
                        return criticalMemNumbers;
                    }
                });

        clusterSeen = new ArrayList<String>();
        com.codahale.metrics.Meter dropwizardMeter = new com.codahale.metrics.Meter();
        this.dataThroughput = getRuntimeContext()
                .getMetricGroup()
                .addGroup("EverestMetrics")
                .meter("data_throughput", new DropwizardMeterWrapper(dropwizardMeter));
    }

    /**
     * BUG BUG BUG
     * @param everestCollectorData
     * @return
     * @throws Exception
     */
    @Override
    public boolean filter(EverestCollectorData everestCollectorData) throws Exception {
        if(dataThroughput != null)
            dataThroughput.markEvent();

        boolean isValid = true;
        List<EverestCollectorData.CpuData> cpuDatas = everestCollectorData.getCpuData();
        for(EverestCollectorData.CpuData cpuData : cpuDatas) {
//            System.out.println("\nValidValueFilter CPU ID -> " + cpuData.getId());
//            System.out.println("\nValidValueFilter Data CPU VALUE -> " + cpuData.getValue());
            isValid = isValid && cpuData.getValue() >= EverestDefaultValues.VALID_VALUE_LOW_BOUND && cpuData.getValue() <= EverestDefaultValues.VALID_VALUE_HIGH_BOUND;
        }

        if(validCpuCounter != null && isValid)
            this.validCpuCounter.inc();
        else {
            if (invalidCpuCounter != null) {
                this.invalidCpuCounter.inc();
            }
        }


        if(clusterSeen != null) {
            String cid = everestCollectorData.getCluster_id();
            if(!clusterSeen.contains(cid)) {
                clusterSeen.add(cid);
                clusterNumbers = clusterSeen.size();
            }
        }
        return isValid;
    }
}