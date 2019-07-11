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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A {@link FilterFunction} that continuously filter bad values (lower than lower bound or higher than higher bound).
 */
public class CategoryFilter extends RichFilterFunction<EverestCollectorDataT<Double, Double>> {
    private static final long serialVersionUID = 5273579696640156346L;
    /**
     * Metrics for Prometheus
     */
    private transient Set<String> setCpuCriticalNumbers;
    private transient Set<String> setCpuHighNumbers;
    private transient Set<String> setCpuRegularNumbers;
    private transient Set<String> setCpuLowNumbers;
    private transient Set<String> setMemCriticalNumbers;
    private transient Set<String> setMemHighNumbers;
    private transient Set<String> setMemRegularNumbers;
    private transient Set<String> setMemLowNumbers;
    private transient Set<String> setNetCriticalNumbers;
    private transient Set<String> setNetHighNumbers;
    private transient Set<String> setNetRegularNumbers;
    private transient Set<String> setNetLowNumbers;

    private transient int _CpuCriticalNumbers;
    private transient int _CpuHighNumbers;
    private transient int _CpuRegularNumbers;
    private transient int _CpuLowNumbers;
    private transient int _MemCriticalNumbers;
    private transient int _MemHighNumbers;
    private transient int _MemRegularNumbers;
    private transient int _MemLowNumbers;
    private transient int _NetCriticalNumbers;
    private transient int _NetHighNumbers;
    private transient int _NetRegularNumbers;
    private transient int _NetLowNumbers;

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



    }

    @Override
    public void open(Configuration config) {

        freqs = new HashMap();

        setCpuCriticalNumbers = new HashSet<String>();
        setCpuHighNumbers = new HashSet<String>();
        setCpuLowNumbers = new HashSet<String>();
        setCpuRegularNumbers = new HashSet<String>();
        setMemCriticalNumbers = new HashSet<String>();
        setMemHighNumbers = new HashSet<String>();
        setMemLowNumbers = new HashSet<String>();
        setMemRegularNumbers = new HashSet<String>();
        setNetCriticalNumbers = new HashSet<String>();
        setNetHighNumbers = new HashSet<String>();
        setNetLowNumbers = new HashSet<String>();
        setNetRegularNumbers = new HashSet<String>();

        _CpuCriticalNumbers = 0;
        _CpuHighNumbers = 0;
        _CpuLowNumbers = 0;
        _CpuRegularNumbers = 0;
        _MemCriticalNumbers = 0;
        _MemHighNumbers = 0;
        _MemLowNumbers = 0;
        _MemRegularNumbers = 0;
        _NetCriticalNumbers = 0;
        _NetHighNumbers = 0;
        _NetLowNumbers = 0;
        _NetRegularNumbers = 0;
        getRuntimeContext()
                .getMetricGroup()
                .addGroup(EverestDefaultValues.EVEREST_METRICS_GROUP)
                .gauge(EverestDefaultValues.CPU_CRITICAL_NUMBERS, new Gauge<Integer>() {
                    @Override
                    public Integer getValue() {
                        return _CpuCriticalNumbers;
                    }
                });
        getRuntimeContext()
                .getMetricGroup()
                .addGroup(EverestDefaultValues.EVEREST_METRICS_GROUP)
                .gauge(EverestDefaultValues.CPU_HIGH_NUMBERS, new Gauge<Integer>() {
                    @Override
                    public Integer getValue() {
                        return _CpuHighNumbers;
                    }
                });
        getRuntimeContext()
                .getMetricGroup()
                .addGroup(EverestDefaultValues.EVEREST_METRICS_GROUP)
                .gauge(EverestDefaultValues.CPU_REGULAR_NUMBERS, new Gauge<Integer>() {
                    @Override
                    public Integer getValue() {
                        return _CpuRegularNumbers;
                    }
                });
        getRuntimeContext()
                .getMetricGroup()
                .addGroup(EverestDefaultValues.EVEREST_METRICS_GROUP)
                .gauge(EverestDefaultValues.CPU_LOW_NUMBERS, new Gauge<Integer>() {
                    @Override
                    public Integer getValue() {
                        return _CpuLowNumbers;
                    }
                });

        getRuntimeContext()
                .getMetricGroup()
                .addGroup(EverestDefaultValues.EVEREST_METRICS_GROUP)
                .gauge(EverestDefaultValues.MEM_CRITICAL_NUMBERS, new Gauge<Integer>() {
                    @Override
                    public Integer getValue() {
                        return _MemCriticalNumbers;
                    }
                });
        getRuntimeContext()
                .getMetricGroup()
                .addGroup(EverestDefaultValues.EVEREST_METRICS_GROUP)
                .gauge(EverestDefaultValues.MEM_HIGH_NUMBERS, new Gauge<Integer>() {
                    @Override
                    public Integer getValue() {
                        return _MemHighNumbers;
                    }
                });
        getRuntimeContext()
                .getMetricGroup()
                .addGroup(EverestDefaultValues.EVEREST_METRICS_GROUP)
                .gauge(EverestDefaultValues.MEM_REGULAR_NUMBERS, new Gauge<Integer>() {
                    @Override
                    public Integer getValue() {
                        return _MemRegularNumbers;
                    }
                });
        getRuntimeContext()
                .getMetricGroup()
                .addGroup(EverestDefaultValues.EVEREST_METRICS_GROUP)
                .gauge(EverestDefaultValues.MEM_LOW_NUMBERS, new Gauge<Integer>() {
                    @Override
                    public Integer getValue() {
                        return _MemLowNumbers;
                    }
                });

        getRuntimeContext()
                .getMetricGroup()
                .addGroup(EverestDefaultValues.EVEREST_METRICS_GROUP)
                .gauge(EverestDefaultValues.NET_CRITICAL_NUMBERS, new Gauge<Integer>() {
                    @Override
                    public Integer getValue() {
                        return _NetCriticalNumbers;
                    }
                });
        getRuntimeContext()
                .getMetricGroup()
                .addGroup(EverestDefaultValues.EVEREST_METRICS_GROUP)
                .gauge(EverestDefaultValues.NET_HIGH_NUMBERS, new Gauge<Integer>() {
                    @Override
                    public Integer getValue() {
                        return _NetHighNumbers;
                    }
                });
        getRuntimeContext()
                .getMetricGroup()
                .addGroup(EverestDefaultValues.EVEREST_METRICS_GROUP)
                .gauge(EverestDefaultValues.NET_REGULAR_NUMBERS, new Gauge<Integer>() {
                    @Override
                    public Integer getValue() {
                        return _NetRegularNumbers;
                    }
                });
        getRuntimeContext()
                .getMetricGroup()
                .addGroup(EverestDefaultValues.EVEREST_METRICS_GROUP)
                .gauge(EverestDefaultValues.NET_LOW_NUMBERS, new Gauge<Integer>() {
                    @Override
                    public Integer getValue() {
                        return _NetLowNumbers;
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

    void setNumbers(String cat, EverestCollectorDataT<Double, Double> data) {
        String _data = new String(data.getCluster_id() + "_" + data.getNamespace() + "_" + data.getPodName());
        if(EverestDefaultValues.CATEGORY_CPU_CRITICAL.equals(cat)) {
            setCpuCriticalNumbers.add(_data);
            setCpuRegularNumbers.remove(_data);
            setCpuHighNumbers.remove(_data);
            setCpuLowNumbers.remove(_data);
        } else if(EverestDefaultValues.CATEGORY_CPU_HIGH.equals(cat)) {
            setCpuHighNumbers.add(_data);
            setCpuRegularNumbers.remove(_data);
            setCpuCriticalNumbers.remove(_data);
            setCpuLowNumbers.remove(_data);
        } else if(EverestDefaultValues.CATEGORY_CPU_REGULAR.equals(cat)) {
            setCpuRegularNumbers.add(_data);
            setCpuHighNumbers.remove(_data);
            setCpuCriticalNumbers.remove(_data);
            setCpuLowNumbers.remove(_data);
        } else if(EverestDefaultValues.CATEGORY_CPU_LOW.equals(cat)) {
            setCpuLowNumbers.add(_data);
            setCpuRegularNumbers.remove(_data);
            setCpuCriticalNumbers.remove(_data);
            setCpuHighNumbers.remove(_data);
        } else if(EverestDefaultValues.CATEGORY_MEM_CRITICAL.equals(cat)) {
            setMemCriticalNumbers.add(_data);
            setMemRegularNumbers.remove(_data);
            setMemHighNumbers.remove(_data);
            setMemLowNumbers.remove(_data);
        } else if(EverestDefaultValues.CATEGORY_MEM_HIGH.equals(cat)) {
            setMemHighNumbers.add(_data);
            setMemRegularNumbers.remove(_data);
            setMemCriticalNumbers.remove(_data);
            setMemLowNumbers.remove(_data);
        } else if(EverestDefaultValues.CATEGORY_MEM_REGULAR.equals(cat)) {
            setMemRegularNumbers.add(_data);
            setMemHighNumbers.remove(_data);
            setMemCriticalNumbers.remove(_data);
            setMemLowNumbers.remove(_data);
        } else if(EverestDefaultValues.CATEGORY_MEM_LOW.equals(cat)) {
            setMemLowNumbers.add(_data);
            setMemRegularNumbers.remove(_data);
            setMemCriticalNumbers.remove(_data);
            setMemHighNumbers.remove(_data);
        } else if(EverestDefaultValues.CATEGORY_NET_CRITICAL.equals(cat)) {
            setNetCriticalNumbers.add(_data);
            setNetRegularNumbers.remove(_data);
            setNetHighNumbers.remove(_data);
            setNetLowNumbers.remove(_data);
        } else if(EverestDefaultValues.CATEGORY_NET_HIGH.equals(cat)) {
            setNetHighNumbers.add(_data);
            setNetRegularNumbers.remove(_data);
            setNetCriticalNumbers.remove(_data);
            setNetLowNumbers.remove(_data);
        } else if(EverestDefaultValues.CATEGORY_NET_REGULAR.equals(cat)) {
            setNetRegularNumbers.add(_data);
            setNetHighNumbers.remove(_data);
            setNetCriticalNumbers.remove(_data);
            setNetLowNumbers.remove(_data);
        } else if(EverestDefaultValues.CATEGORY_NET_LOW.equals(cat)) {
            setNetLowNumbers.add(_data);
            setNetRegularNumbers.remove(_data);
            setNetCriticalNumbers.remove(_data);
            setNetHighNumbers.remove(_data);
        }

        _CpuCriticalNumbers = setCpuCriticalNumbers.size();
        _CpuHighNumbers = setCpuHighNumbers.size();
        _CpuLowNumbers = setCpuLowNumbers.size();
        _CpuRegularNumbers = setCpuRegularNumbers.size();
        _MemCriticalNumbers = setMemCriticalNumbers.size();
        _MemHighNumbers = setMemHighNumbers.size();
        _MemLowNumbers = setMemLowNumbers.size();
        _MemRegularNumbers = setMemRegularNumbers.size();
        _NetCriticalNumbers = setNetCriticalNumbers.size();
        _NetHighNumbers = setNetHighNumbers.size();
        _NetLowNumbers = setNetLowNumbers.size();
        _NetRegularNumbers = setNetRegularNumbers.size();


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
            setNumbers(EverestDefaultValues.CATEGORY_CPU_CRITICAL, data);
            System.out.println("CPU Critical percent= " + data.getPercentage() + " numbers= " + setCpuCriticalNumbers);
            return true;
        } else if (typeToFilter.equals(EverestDefaultValues.CATEGORY_CPU_HIGH) && (data.getPercentage() >= _cpuThresholds[1]) && (data.getPercentage() < _cpuThresholds[0])) {
            setNumbers(EverestDefaultValues.CATEGORY_CPU_HIGH, data);
            System.out.println("CPU high percent= " + data.getPercentage() + " numbers= " + setCpuHighNumbers);
            return true;
        } else if (typeToFilter.equals(EverestDefaultValues.CATEGORY_CPU_REGULAR) && (data.getPercentage() >= _cpuThresholds[2]) && (data.getPercentage() < _cpuThresholds[1])) {
            setNumbers(EverestDefaultValues.CATEGORY_CPU_REGULAR, data);
            return true;
        } else if (typeToFilter.equals(EverestDefaultValues.CATEGORY_CPU_LOW) && (data.getPercentage() < _cpuThresholds[2])) {
            setNumbers(EverestDefaultValues.CATEGORY_CPU_LOW, data);
            return true;
        } else if (typeToFilter.equals(EverestDefaultValues.CATEGORY_MEM_CRITICAL) && (data.getPercentage() >= _memThresholds[0])) {
            setNumbers(EverestDefaultValues.CATEGORY_MEM_CRITICAL, data);
            System.out.println("MEM Critical percent= " + data.getPercentage() + " numbers= " + setCpuCriticalNumbers);
            return true;
        } else if (typeToFilter.equals(EverestDefaultValues.CATEGORY_MEM_HIGH) && (data.getPercentage() >= _memThresholds[1]) && (data.getPercentage() < _memThresholds[0])) {
            setNumbers(EverestDefaultValues.CATEGORY_MEM_HIGH, data);
            System.out.println("MEM High percent= " + data.getPercentage() + " numbers= " + setCpuCriticalNumbers);
            return true;
        } else if (typeToFilter.equals(EverestDefaultValues.CATEGORY_MEM_REGULAR) && (data.getPercentage() >= _memThresholds[2]) && (data.getPercentage() < _memThresholds[1])) {
            setNumbers(EverestDefaultValues.CATEGORY_MEM_REGULAR, data);
            return true;
        } else if (typeToFilter.equals(EverestDefaultValues.CATEGORY_MEM_LOW) && (data.getPercentage() < _memThresholds[2])) {
            setNumbers(EverestDefaultValues.CATEGORY_MEM_LOW, data);
            return true;
        } else if (typeToFilter.equals(EverestDefaultValues.CATEGORY_NET_CRITICAL) && (data.getPercentage() >= _netThresholds[0])) {
            setNumbers(EverestDefaultValues.CATEGORY_NET_CRITICAL, data);
            System.out.println("NET Critical percent= " + data.getPercentage() + " numbers= " + setCpuCriticalNumbers);
            return true;
        } else if (typeToFilter.equals(EverestDefaultValues.CATEGORY_NET_HIGH) && (data.getPercentage() >= _netThresholds[1]) && (data.getPercentage() < _netThresholds[0])) {
            setNumbers(EverestDefaultValues.CATEGORY_NET_HIGH, data);
            System.out.println("NET High percent= " + data.getPercentage() + " numbers= " + setCpuCriticalNumbers);
            return true;
        } else if (typeToFilter.equals(EverestDefaultValues.CATEGORY_NET_REGULAR) && (data.getPercentage() >= _netThresholds[2]) && (data.getPercentage() < _netThresholds[1])) {
            setNumbers(EverestDefaultValues.CATEGORY_NET_REGULAR, data);
            return true;
        } else if (typeToFilter.equals(EverestDefaultValues.CATEGORY_NET_LOW) && (data.getPercentage() < _netThresholds[2])) {
            setNumbers(EverestDefaultValues.CATEGORY_NET_LOW, data);
            return true;
        }
//        System.out.println("***** ERROR ***** expected = " + typeToFilter + " received = " + data.getType() + " percent= " + data.getPercentage());
//        System.out.println("***** ERROR *****: unexpected type to filter in RichFilterFunction CategoryFilter Class");
        return false;
    }

}
