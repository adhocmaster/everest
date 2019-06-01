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
import com.futurewei.everest.datatypes.EverestCollectorDataT;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.RichFilterFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.metrics.Gauge;

/**
 * A {@link FilterFunction} that continuously filter bad values (lower than lower bound or higher than higher bound).
 */
public class CategoryFilter extends RichFilterFunction<EverestCollectorDataT<Double, Double>> {
    private static final long serialVersionUID = 5273579696640156346L;
    /**
     * Metrics for Prometheus
     */
    private transient int cpuCriticalNumbers = 0;
    private transient int cpuHighNumbers = 0;
    private transient int cpuRegularNumbers = 0;
    private transient int cpuLowNumbers = 0;
    private transient int memCriticalNumbers = 0;
    private transient int memHighNumbers = 0;
    private transient int memRegularNumbers = 0;
    private transient int memLowNumbers = 0;

    // A type of collection to filter everest data. This will be stored in memory
    // of a task manager
    String typeToFilter;

    public CategoryFilter(String typeToFilter) {
        this.typeToFilter = typeToFilter;
    }

    @Override
    public void open(Configuration config) {
        getRuntimeContext()
                .getMetricGroup()
                .addGroup(EverestDefaultValues.EVEREST_METRICS_GROUP)
                .gauge(EverestDefaultValues.CPU_CRITICAL_NUMBERS, new Gauge<Integer>() {
                    @Override
                    public Integer getValue() {
                        return cpuCriticalNumbers;
                    }
                });
        getRuntimeContext()
                .getMetricGroup()
                .addGroup(EverestDefaultValues.EVEREST_METRICS_GROUP)
                .gauge(EverestDefaultValues.CPU_HIGH_NUMBERS, new Gauge<Integer>() {
                    @Override
                    public Integer getValue() {
                        return cpuHighNumbers;
                    }
                });
        getRuntimeContext()
                .getMetricGroup()
                .addGroup(EverestDefaultValues.EVEREST_METRICS_GROUP)
                .gauge(EverestDefaultValues.CPU_REGULAR_NUMBERS, new Gauge<Integer>() {
                    @Override
                    public Integer getValue() {
                        return cpuRegularNumbers;
                    }
                });
        getRuntimeContext()
                .getMetricGroup()
                .addGroup(EverestDefaultValues.EVEREST_METRICS_GROUP)
                .gauge(EverestDefaultValues.CPU_LOW_NUMBERS, new Gauge<Integer>() {
                    @Override
                    public Integer getValue() {
                        return cpuLowNumbers;
                    }
                });

        getRuntimeContext()
                .getMetricGroup()
                .addGroup(EverestDefaultValues.EVEREST_METRICS_GROUP)
                .gauge(EverestDefaultValues.MEM_CRITICAL_NUMBERS, new Gauge<Integer>() {
                    @Override
                    public Integer getValue() {
                        return memCriticalNumbers;
                    }
                });
        getRuntimeContext()
                .getMetricGroup()
                .addGroup(EverestDefaultValues.EVEREST_METRICS_GROUP)
                .gauge(EverestDefaultValues.MEM_HIGH_NUMBERS, new Gauge<Integer>() {
                    @Override
                    public Integer getValue() {
                        return memHighNumbers;
                    }
                });
        getRuntimeContext()
                .getMetricGroup()
                .addGroup(EverestDefaultValues.EVEREST_METRICS_GROUP)
                .gauge(EverestDefaultValues.MEM_REGULAR_NUMBERS, new Gauge<Integer>() {
                    @Override
                    public Integer getValue() {
                        return memRegularNumbers;
                    }
                });
        getRuntimeContext()
                .getMetricGroup()
                .addGroup(EverestDefaultValues.EVEREST_METRICS_GROUP)
                .gauge(EverestDefaultValues.MEM_LOW_NUMBERS, new Gauge<Integer>() {
                    @Override
                    public Integer getValue() {
                        return memLowNumbers;
                    }
                });
    }

    /**
     *
     * @param data the incoming data
     * @return true the data is in the range of values
     * @throws Exception if exception happens
     */
    @Override
    public boolean filter(EverestCollectorDataT<Double, Double> data) throws Exception {
        if(typeToFilter.equals(EverestDefaultValues.CATEGORY_CPU_CRITICAL) && data.getPercentage() >= EverestDefaultValues.CPU_THRESHOLD_CRITICAL) {
            System.out.println("CPU CRITICAL: Cluster=" + data.getCluster_id() + " Pod=" + data.getPodName() + "@" + data.getNamespace() + " P=" +
                    data.getPercentage() + "% V=" + data.getValue());
            cpuCriticalNumbers++;
            return(true);
        } else if (typeToFilter.equals(EverestDefaultValues.CATEGORY_CPU_HIGH) && (data.getPercentage() >= EverestDefaultValues.CPU_THRESHOLD_HIGH && data.getPercentage() < EverestDefaultValues.CPU_THRESHOLD_CRITICAL)) {
            System.out.println("CPU HIGH: Cluster=" + data.getCluster_id() + " Pod=" + data.getPodName() + "@" + data.getNamespace() + " P=" +
                    data.getPercentage() + "% V=" + data.getValue());
            cpuHighNumbers++;
            return(true);
        } else if (typeToFilter.equals(EverestDefaultValues.CATEGORY_CPU_REGULAR) && (data.getPercentage() >= EverestDefaultValues.CPU_THRESHOLD_REGULAR && data.getPercentage() < EverestDefaultValues.CPU_THRESHOLD_HIGH)) {
            System.out.println("CPU REGULAR: Cluster=" + data.getCluster_id() + " Pod=" + data.getPodName() + "@" + data.getNamespace() + " P=" +
                    data.getPercentage() + "% V=" + data.getValue());
            cpuRegularNumbers++;
            return(true);
        } else if (typeToFilter.equals(EverestDefaultValues.CATEGORY_CPU_LOW) && (data.getPercentage() < EverestDefaultValues.CPU_THRESHOLD_REGULAR)) {
            System.out.println("CPU LOW: Cluster=" + data.getCluster_id() + " Pod=" + data.getPodName() + "@" + data.getNamespace() + " P=" +
                    data.getPercentage() + "% V=" + data.getValue());
            cpuLowNumbers++;
            return (true);
        } else if(typeToFilter.equals(EverestDefaultValues.CATEGORY_MEM_CRITICAL) && data.getPercentage() >= EverestDefaultValues.MEM_THRESHOLD_CRITICAL) {
            System.out.println("MEM CRITICAL: Cluster=" + data.getCluster_id() + " Pod=" + data.getPodName() + "@" + data.getNamespace() + " P=" +
                    data.getPercentage() + "% V=" + data.getValue());
            memCriticalNumbers++;
            return(true);
        } else if (typeToFilter.equals(EverestDefaultValues.CATEGORY_MEM_HIGH) && (data.getPercentage() >= EverestDefaultValues.MEM_THRESHOLD_HIGH && data.getPercentage() < EverestDefaultValues.MEM_THRESHOLD_CRITICAL)) {
            System.out.println("MEM HIGH: Cluster=" + data.getCluster_id() + " Pod=" + data.getPodName() + "@" + data.getNamespace() + " P=" +
                    data.getPercentage() + "% V=" + data.getValue());
            memHighNumbers++;
            return(true);
        } else if (typeToFilter.equals(EverestDefaultValues.CATEGORY_MEM_REGULAR) && (data.getPercentage() >= EverestDefaultValues.MEM_THRESHOLD_REGULAR && data.getPercentage() < EverestDefaultValues.MEM_THRESHOLD_HIGH)) {
            System.out.println("MEM REGULAR: Cluster=" + data.getCluster_id() + " Pod=" + data.getPodName() + "@" + data.getNamespace() + " P=" +
                    data.getPercentage() + "% V=" + data.getValue());
            memRegularNumbers++;
            return(true);
        } else if (typeToFilter.equals(EverestDefaultValues.CATEGORY_MEM_LOW) && (data.getPercentage() < EverestDefaultValues.MEM_THRESHOLD_REGULAR)) {
            System.out.println("MEM LOW: Cluster=" + data.getCluster_id() + " Pod=" + data.getPodName() + "@" + data.getNamespace() + " P=" +
                    data.getPercentage() + "% V=" + data.getValue());
            memLowNumbers++;
            return (true);
        } else {
//            System.out.println("***** ERROR ***** expected = " + typeToFilter + " received = " + data.getPercentage());
//            System.out.println("***** ERROR *****: unexpected type to filter in RichFilterFunction CategoryFilter Class");
//            throw (new Exception("unexpected type to filter in RichFilterFunction CategoryFilter Class " + " received = " + data.getPercentage()));
            return false;
        }
    }
}