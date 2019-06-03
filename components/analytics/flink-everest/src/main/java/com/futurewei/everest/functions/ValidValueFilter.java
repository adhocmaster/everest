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
import org.apache.flink.dropwizard.metrics.DropwizardMeterWrapper;
import org.apache.flink.metrics.Counter;
import org.apache.flink.metrics.Gauge;
import org.apache.flink.metrics.Meter;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link FilterFunction} that continuously filter bad values (lower than lower bound or higher than higher bound).
 */
public class ValidValueFilter extends RichFilterFunction<EverestCollectorDataT<Double, Double>> {
    private static final long serialVersionUID = 8273479696640156346L;
    /**
     * Metrics for Prometheus
     */
    private transient Counter validCpuCounter;
    private transient Counter invalidCpuCounter;
    private transient Counter validMemCounter;
    private transient Counter invalidMemCounter;
    private transient Counter validNetCounter;
    private transient Counter invalidNetCounter;

    // A type of collection to store everest data. This will be stored in memory
    // of a task manager
    String typeToCollect;

    public ValidValueFilter(String typeToCollect) {
        this.typeToCollect = typeToCollect;
    }

    @Override
    public void open(Configuration config) {
        this.validCpuCounter = getRuntimeContext()
                .getMetricGroup()
                .addGroup(EverestDefaultValues.EVEREST_METRICS_GROUP)
                .counter(EverestDefaultValues.VALID_CPU_COUNTER);
        this.invalidCpuCounter = getRuntimeContext()
                .getMetricGroup()
                .addGroup(EverestDefaultValues.EVEREST_METRICS_GROUP)
                .counter(EverestDefaultValues.INVALID_CPU_COUNTER);
        this.validMemCounter = getRuntimeContext()
                .getMetricGroup()
                .addGroup(EverestDefaultValues.EVEREST_METRICS_GROUP)
                .counter(EverestDefaultValues.VALID_MEM_COUNTER);
        this.invalidMemCounter = getRuntimeContext()
                .getMetricGroup()
                .addGroup(EverestDefaultValues.EVEREST_METRICS_GROUP)
                .counter(EverestDefaultValues.INVALID_MEM_COUNTER);
        this.validNetCounter = getRuntimeContext()
                .getMetricGroup()
                .addGroup(EverestDefaultValues.EVEREST_METRICS_GROUP)
                .counter(EverestDefaultValues.VALID_NET_COUNTER);
        this.invalidNetCounter = getRuntimeContext()
                .getMetricGroup()
                .addGroup(EverestDefaultValues.EVEREST_METRICS_GROUP)
                .counter(EverestDefaultValues.INVALID_NET_COUNTER);
    }

    /**
     *
     * @param data the incoming data
     * @return true the data is in the range of values
     * @throws Exception if exception happens
     */
    @Override
    public boolean filter(EverestCollectorDataT<Double, Double> data) throws Exception {
        boolean isValid = true;


        if(typeToCollect.equals(EverestDefaultValues.TYPE_TO_COLLECT_CPU)) {
            isValid = isValid && data.getPercentage() >= EverestDefaultValues.VALID_VALUE_LOW_BOUND && data.getPercentage() <= EverestDefaultValues.VALID_VALUE_HIGH_BOUND;
            if(validCpuCounter != null && isValid) {
                this.validCpuCounter.inc();
            } else {
                if (invalidCpuCounter != null) {
                    this.invalidCpuCounter.inc();
                }
            }

        } else if(typeToCollect.equals(EverestDefaultValues.TYPE_TO_COLLECT_MEM)) {
            isValid = isValid && data.getPercentage() >= EverestDefaultValues.VALID_VALUE_LOW_BOUND && data.getPercentage() <= EverestDefaultValues.VALID_VALUE_HIGH_BOUND;
            if(validMemCounter != null && isValid)
                this.validMemCounter.inc();
            else {
                if (invalidMemCounter != null) {
                    this.invalidMemCounter.inc();
                }
            }
        } else if(typeToCollect.equals(EverestDefaultValues.TYPE_TO_COLLECT_NET)) {
            isValid = isValid && data.getPercentage() >= EverestDefaultValues.VALID_VALUE_LOW_BOUND && data.getPercentage() <= EverestDefaultValues.VALID_VALUE_HIGH_BOUND;
            if(validNetCounter != null && isValid)
                this.validNetCounter.inc();
            else {
                if (invalidNetCounter != null) {
                    this.invalidNetCounter.inc();
                }
            }
        } else {
            System.out.println("***** ERROR *****: unexpected type to filter in RichFilterFunction ValidValueFilter Class");
            throw (new Exception("unexpected type to filter in RichFilterFunction ValidValueFilter Class"));
        }

        return isValid;
    }
}