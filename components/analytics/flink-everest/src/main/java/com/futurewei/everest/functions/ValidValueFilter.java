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
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.RichFilterFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.dropwizard.metrics.DropwizardMeterWrapper;
import org.apache.flink.metrics.Counter;
import org.apache.flink.metrics.Meter;
import org.apache.flink.metrics.Gauge;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link FilterFunction} that continuously filter bad values (lower than lower bound or higher than higher bound).
 */
public class ValidValueFilter extends RichFilterFunction<KafkaEvent<String, Double, Double>> {

    private static final long serialVersionUID = 8273479696640156346L;
    private transient Counter validCounter;
    private transient Counter invalidCounter;
    private transient Meter valueThroughput;
    private transient int sensorNumbers = 0;
    private transient int gwNumbers = 0;
    private transient int invalidSensorNumbers;
    private transient List<String> gwSeen;
    private transient List<String> sensorSeen;
    private transient List<String> invalidSensorSeen;
    private transient int criticalSensorNumbers = 0;

    @Override
    public void open(Configuration config) {
        this.validCounter = getRuntimeContext()
                .getMetricGroup()
                .addGroup("EliotMetrics")
                .counter("number_valid_values");
        this.invalidCounter = getRuntimeContext()
                .getMetricGroup()
                .addGroup("EliotMetrics")
                .counter("number_invalid_values");
        getRuntimeContext()
                .getMetricGroup()
                .addGroup("EliotMetrics")
                .gauge("number_gw", new Gauge<Integer>() {
                    @Override
                    public Integer getValue() {
                        return gwNumbers;
                    }
        });
        getRuntimeContext()
                .getMetricGroup()
                .addGroup("EliotMetrics")
                .gauge("number_sensor", new Gauge<Integer>() {
                    @Override
                    public Integer getValue() {
                        return sensorNumbers;
                    }
                });

        getRuntimeContext()
                .getMetricGroup()
                .addGroup("EliotMetrics")
                .gauge("number_invalid_sensor", new Gauge<Integer>() {
                    @Override
                    public Integer getValue() {
                        return invalidSensorNumbers;
                    }
        });

        getRuntimeContext()
                .getMetricGroup()
                .addGroup("EliotMetrics")
                .gauge("number_critical_sensor", new Gauge<Integer>() {
                    @Override
                    public Integer getValue() {
                        return criticalSensorNumbers;
                    }
                });
        gwSeen = new ArrayList<String>();
        sensorSeen = new ArrayList<String>();
        invalidSensorSeen = new ArrayList<String>();
        com.codahale.metrics.Meter dropwizardMeter = new com.codahale.metrics.Meter();
        this.valueThroughput = getRuntimeContext()
                .getMetricGroup()
                .addGroup("EliotMetrics")
                .meter("value_throughput", new DropwizardMeterWrapper(dropwizardMeter));
    }

    @Override
    public boolean filter(KafkaEvent<String, Double, Double> event) throws Exception {
        boolean isValid = event.getValue() >= EverestDefaultValues.VALID_VALUE_LOW_BOUND && event.getValue() <= EverestDefaultValues.VALID_VALUE_HIGH_BOUND &&
                event.getValue0() >= EverestDefaultValues.VALID_VALUE_LOW_BOUND && event.getValue0() <= EverestDefaultValues.VALID_VALUE_HIGH_BOUND;

        if(valueThroughput != null)
            valueThroughput.markEvent();

        if(validCounter != null && isValid)
            this.validCounter.inc();
        else {
            if (invalidCounter != null) {
                this.invalidCounter.inc();
                invalidSensorNumbers++;
            }
        }

        if(gwSeen != null) {
            String sid = event.getId();
            String[] parts = sid.split(":");
            String gw = parts[0]; // gateway ID
            if(!gwSeen.contains(gw)) {
                gwSeen.add(gw);
                gwNumbers = gwSeen.size();
            }
            if(!sensorSeen.contains(sid)) {
                sensorSeen.add(sid);
                sensorNumbers = sensorSeen.size();
            }

            if(!isValid) {
                if(!invalidSensorSeen.contains(sid)) {
                    invalidSensorSeen.add(sid);
                }
            } else {
                if(invalidSensorSeen.contains(sid)) {
                    invalidSensorSeen.remove(sid);
                }
            }
            invalidSensorNumbers = invalidSensorSeen.size();
        }
        return isValid;
    }
}