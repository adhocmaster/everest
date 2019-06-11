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

import com.futurewei.everest.datatypes.EverestCollectorDataT;
import org.apache.flink.api.common.functions.ReduceFunction;

public class MaxValueReducer implements ReduceFunction<EverestCollectorDataT<Double, Double>> {
    private static final long serialVersionUID = 3633307914998385997L;

    @Override
    public EverestCollectorDataT<Double, Double> reduce(EverestCollectorDataT<Double, Double> event0,
                                                        EverestCollectorDataT<Double, Double> event1) throws Exception {
//        System.out.println("REDUCE Data Cluster ID" + event0.getCluster_id());
//        System.out.println("REDUCE Data TS" + event0.getTs());

        if(event0.getPercentage() > event1.getPercentage() || event0.getValue() > event1.getValue()) {
            return new EverestCollectorDataT<Double, Double>(event0.getCluster_id(), event0.getContainerName(),
                    event0.getPodName(), event0.getNamespace(), event0.getMetric(), event0.getType(), event0.getTs(), event0.getValue(), event0.getPercentage());
        } else {
            return new EverestCollectorDataT<Double, Double>(event1.getCluster_id(), event1.getContainerName(),
                    event1.getPodName(), event1.getNamespace(), event1.getMetric(), event1.getType(), event1.getTs(), event1.getValue(), event1.getPercentage());
        }
    }
}