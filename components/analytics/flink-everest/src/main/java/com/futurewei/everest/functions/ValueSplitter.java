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
import org.apache.flink.streaming.api.collector.selector.OutputSelector;

import java.util.ArrayList;
import java.util.List;

public class ValueSplitter implements OutputSelector<EverestCollectorDataT<Double, Double>> {
    private static final long serialVersionUID = 5520752323589420741L;

    String typeToCollect;

    public ValueSplitter(String typeToCollect) {
        this.typeToCollect = typeToCollect;
    }

    @Override
    public Iterable<String> select(EverestCollectorDataT<Double, Double> data) {
        List<String> output = new ArrayList<String>();
        Double percent = data.getPercentage();
        String cat = "low";

        if(typeToCollect.equals("cpu")) {
            if (percent >= EverestDefaultValues.CPU_THRESHOLD_CRITICAL) {
                cat = "critical";
            } else if (percent >= EverestDefaultValues.CPU_THRESHOLD_HIGH && percent < EverestDefaultValues.CPU_THRESHOLD_CRITICAL) {
                cat = "high";
            } else if (percent >= EverestDefaultValues.CPU_THRESHOLD_REGULAR && percent < EverestDefaultValues.CPU_THRESHOLD_HIGH) {
                cat = "regular";
            } else {
                cat = "low";
            }
        } else if(typeToCollect.equals("mem")) {
            if (percent >= EverestDefaultValues.MEM_THRESHOLD_CRITICAL) {
                cat = "critical";
            } else if (percent >= EverestDefaultValues.MEM_THRESHOLD_HIGH && percent < EverestDefaultValues.MEM_THRESHOLD_CRITICAL) {
                cat = "high";
            } else if (percent >= EverestDefaultValues.MEM_THRESHOLD_REGULAR && percent < EverestDefaultValues.MEM_THRESHOLD_HIGH) {
                cat = "regular";
            } else {
                cat = "low";
            }
        } else {
            System.out.println("***** ERROR *****: unexpected type to filter in RichFlatMapFunction ValueFlatMap Class");
        }
        output.add(cat);

        return output;

    }
}