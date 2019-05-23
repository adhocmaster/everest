/*
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

package com.futurewei.everest.functions;

import com.futurewei.everest.datatypes.KafkaEvent;
import org.apache.flink.api.common.functions.ReduceFunction;

public class MaxDiffReducer implements ReduceFunction<KafkaEvent<String, Double, Double>> {


    private static final long serialVersionUID = 3633307914998385997L;

    @Override
    public KafkaEvent<String, Double, Double> reduce(KafkaEvent<String, Double, Double> event0,
                                         KafkaEvent<String, Double, Double> event1) throws Exception {
        Double diffEvent1 = event1.getValue0() - event1.getValue();
        Double absDiff1 = diffEvent1 >= 0 ? diffEvent1 : -diffEvent1;
        Double diffEvent0 = event0.getValue0() - event0.getValue();
        Double absDiff0 = diffEvent0 >= 0 ? diffEvent0 : -diffEvent0;
        if(absDiff1 > absDiff0)
           return new KafkaEvent<>(event1.getId(), event1.getValue(), event1.getValue0(), event1.getTimestamp(), event1.getDescription());
        else
            return new KafkaEvent<>(event0.getId(), event0.getValue(), event0.getValue0(), event0.getTimestamp(), event0.getDescription());
    }
}
