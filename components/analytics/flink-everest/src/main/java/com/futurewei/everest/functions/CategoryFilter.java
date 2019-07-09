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

import java.util.HashMap;
import java.util.Map;

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
    private transient int netCriticalNumbers = 0;
    private transient int netHighNumbers = 0;
    private transient int netRegularNumbers = 0;
    private transient int netLowNumbers = 0;

    // Thresholds
    private static final transient int THRESHOLD = 11 - 8;

    // data structure to keep the information
    private Map<String, Integer> freqs;

    // A type of collection to filter everest data. This will be stored in memory
    // of a task manager
    String typeToFilter;
    long memPercentThreshold;
    long netPercentThreshold;
    double cpuPercentThreshold;
    long[] _memThresholds;
    long[] _netThresholds;
    double[] _cpuThresholds;

    public CategoryFilter(String typeToFilter, long memThreshold, long netThreshold, double cpuThreshold) {

        this.typeToFilter = typeToFilter;
        this.cpuPercentThreshold = cpuThreshold;
        this.memPercentThreshold = memThreshold; // 1000000
        this.netPercentThreshold = netThreshold; // 1000000
        if(cpuThreshold == EverestDefaultValues.CPU_RATE_THRESHOLD_CRITICAL) {
            _cpuThresholds = new double[] {EverestDefaultValues.CPU_RATE_THRESHOLD_CRITICAL, EverestDefaultValues.CPU_RATE_THRESHOLD_HIGH, EverestDefaultValues.CPU_RATE_THRESHOLD_REGULAR};
        } else {
            _cpuThresholds = new double[]{cpuThreshold, cpuThreshold - 0.1, cpuThreshold - 0.2};
        }
        if(memThreshold == EverestDefaultValues.MEM_RATE_THRESHOLD_CRITICAL) {
            _memThresholds = new long[] {EverestDefaultValues.MEM_RATE_THRESHOLD_CRITICAL, EverestDefaultValues.MEM_RATE_THRESHOLD_HIGH, EverestDefaultValues.MEM_RATE_THRESHOLD_REGULAR};
        } else {
            _memThresholds = new long[]{memThreshold, memThreshold - EverestDefaultValues.MB, memThreshold - (2 * EverestDefaultValues.MB)};
        }
        if(netThreshold == EverestDefaultValues.NET_RATE_THRESHOLD_CRITICAL) {
            _netThresholds = new long[] {EverestDefaultValues.NET_RATE_THRESHOLD_CRITICAL, EverestDefaultValues.NET_RATE_THRESHOLD_HIGH, EverestDefaultValues.NET_RATE_THRESHOLD_REGULAR};
        } else {
            _netThresholds = new long[]{netThreshold, netThreshold - EverestDefaultValues.MB, netThreshold - (2 * EverestDefaultValues.MB)};
        }


        freqs = new HashMap();
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

        getRuntimeContext()
                .getMetricGroup()
                .addGroup(EverestDefaultValues.EVEREST_METRICS_GROUP)
                .gauge(EverestDefaultValues.NET_CRITICAL_NUMBERS, new Gauge<Integer>() {
                    @Override
                    public Integer getValue() {
                        return netCriticalNumbers;
                    }
                });
        getRuntimeContext()
                .getMetricGroup()
                .addGroup(EverestDefaultValues.EVEREST_METRICS_GROUP)
                .gauge(EverestDefaultValues.NET_HIGH_NUMBERS, new Gauge<Integer>() {
                    @Override
                    public Integer getValue() {
                        return netHighNumbers;
                    }
                });
        getRuntimeContext()
                .getMetricGroup()
                .addGroup(EverestDefaultValues.EVEREST_METRICS_GROUP)
                .gauge(EverestDefaultValues.NET_REGULAR_NUMBERS, new Gauge<Integer>() {
                    @Override
                    public Integer getValue() {
                        return netRegularNumbers;
                    }
                });
        getRuntimeContext()
                .getMetricGroup()
                .addGroup(EverestDefaultValues.EVEREST_METRICS_GROUP)
                .gauge(EverestDefaultValues.NET_LOW_NUMBERS, new Gauge<Integer>() {
                    @Override
                    public Integer getValue() {
                        return netLowNumbers;
                    }
                });

    }

    private boolean _filter0(EverestCollectorDataT<Double, Double> data) {
        String _data = new String(data.getCluster_id() + "_" + data.getNamespace() + "_" + data.getPodName());
        if(freqs.containsKey(_data) == false) {
            freqs.put(_data, new Integer(1));
        } else {
            Integer nval = freqs.get(_data).intValue() + 1;
            freqs.put(_data, nval);
        }
        if(freqs.get(_data).intValue() > CategoryFilter.THRESHOLD ) {
            freqs.put(_data, new Integer(1));

            System.out.println("threshold metric=" + data.getMetric() + " Pod=" + data.getPodName() + "@" + data.getNamespace() + " P=" +
                    data.getPercentage() + "% V=" + data.getValue());
            return true;
        }
        return false;
    }

    /**
     *
     * @param data the incoming data
     * @return true the data is in the range of values
     * @throws Exception if exception happens
     */
    @Override
    public boolean filter(EverestCollectorDataT<Double, Double> data) throws Exception {

        /**
         * BUG BUG we need to use 'metric'
         */
//        if(data.getPercentage() > 100) {
//            System.out.println("catfilter=" + typeToFilter + " metric=" + data.getMetric() + " P=" + data.getPercentage() + "%");
//        }
        if (typeToFilter.equals(EverestDefaultValues.CATEGORY_CPU_CRITICAL) && (data.getPercentage() >= _cpuThresholds[0])) {
            cpuCriticalNumbers++;
            System.out.println("CPU Critical percent= " + data.getPercentage() + " numbers= " + cpuCriticalNumbers);
            return true;
        } else if (typeToFilter.equals(EverestDefaultValues.CATEGORY_CPU_HIGH) && (data.getPercentage() >= _cpuThresholds[1]) && (data.getPercentage() < _cpuThresholds[0])) {
            cpuHighNumbers++;
            System.out.println("CPU high percent= " + data.getPercentage() + " numbers= " + cpuHighNumbers);
            return true;
        } else if (typeToFilter.equals(EverestDefaultValues.CATEGORY_CPU_REGULAR) && (data.getPercentage() >= _cpuThresholds[2]) && (data.getPercentage() < _cpuThresholds[1])) {
            cpuRegularNumbers++;
            return true;
        } else if (typeToFilter.equals(EverestDefaultValues.CATEGORY_CPU_LOW) && (data.getPercentage() < _cpuThresholds[2])) {
            cpuLowNumbers++;
            return true;
        } else if (typeToFilter.equals(EverestDefaultValues.CATEGORY_MEM_CRITICAL) && (data.getPercentage() >= _memThresholds[0])) {
            memCriticalNumbers++;
            System.out.println("MEM Critical percent= " + data.getPercentage() + " numbers= " + cpuCriticalNumbers);
            return true;
        } else if (typeToFilter.equals(EverestDefaultValues.CATEGORY_MEM_HIGH) && (data.getPercentage() >= _memThresholds[1]) && (data.getPercentage() < _memThresholds[0])) {
            memHighNumbers++;
            System.out.println("MEM High percent= " + data.getPercentage() + " numbers= " + cpuCriticalNumbers);
            return true;
        } else if (typeToFilter.equals(EverestDefaultValues.CATEGORY_MEM_REGULAR) && (data.getPercentage() >= _memThresholds[2]) && (data.getPercentage() < _memThresholds[1])) {
            memRegularNumbers++;
            return true;
        } else if (typeToFilter.equals(EverestDefaultValues.CATEGORY_MEM_LOW) && (data.getPercentage() < _memThresholds[2])) {
            memLowNumbers++;
            return true;
        } else if (typeToFilter.equals(EverestDefaultValues.CATEGORY_NET_CRITICAL) && (data.getPercentage() >= _netThresholds[0])) {
            netCriticalNumbers++;
            System.out.println("NET Critical percent= " + data.getPercentage() + " numbers= " + cpuCriticalNumbers);
            return true;
        } else if (typeToFilter.equals(EverestDefaultValues.CATEGORY_NET_HIGH) && (data.getPercentage() >= _netThresholds[1]) && (data.getPercentage() < _netThresholds[0])) {
            netHighNumbers++;
            System.out.println("NET High percent= " + data.getPercentage() + " numbers= " + cpuCriticalNumbers);
            return true;
        } else if (typeToFilter.equals(EverestDefaultValues.CATEGORY_NET_REGULAR) && (data.getPercentage() >= _netThresholds[2]) && (data.getPercentage() < _netThresholds[1])) {
            netRegularNumbers++;
            return true;
        } else if (typeToFilter.equals(EverestDefaultValues.CATEGORY_NET_LOW) && (data.getPercentage() < _netThresholds[2])) {
            netLowNumbers++;
            return true;
        }
//        System.out.println("***** ERROR ***** expected = " + typeToFilter + " received = " + data.getType() + " percent= " + data.getPercentage());
//        System.out.println("***** ERROR *****: unexpected type to filter in RichFilterFunction CategoryFilter Class");
        return false;
    }

}
