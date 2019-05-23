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

import com.futurewei.everest.EverestDefaultValues;
import com.futurewei.everest.datatypes.KafkaEvent;
import org.apache.flink.streaming.api.collector.selector.OutputSelector;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;

import java.util.ArrayList;
import java.util.List;

/**
 * A custom {@link AssignerWithPeriodicWatermarks}, that simply assumes that the input stream
 * records are strictly ascending.
 *
 * <p>Flink also ships some built-in convenience assigners, such as the
 * {@link BoundedOutOfOrdernessTimestampExtractor} and {@link AscendingTimestampExtractor}
 */
public class ValueSplitter implements OutputSelector<KafkaEvent<String, Double, Double>> {

    private static final long serialVersionUID = 5520752323589420741L;

    @Override
    public Iterable<String> select(KafkaEvent<String, Double, Double> event) {
        List<String> output = new ArrayList<String>();
        Double diff = event.getValue0() - event.getValue();
        if (diff <= EverestDefaultValues.THRESHOLD_CRITICAL) {
            output.add("critical");
        } else if (diff < EverestDefaultValues.THRESHOLD_HIGH && diff > EverestDefaultValues.THRESHOLD_CRITICAL) {
            output.add("high");
        } else {
            output.add("low");
        }
        return output;

    }
}

