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

import com.futurewei.everest.datatypes.EverestCollectorData;
import org.apache.flink.streaming.api.functions.AssignerWithPunctuatedWatermarks;
import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.watermark.Watermark;

import javax.annotation.Nullable;

/**
 * A custom {@link AssignerWithPunctuatedWatermarks}, that simply assumes that the input stream
 * records are strictly ascending.
 *
 * <p>Flink also ships some built-in convenience assigners, such as the
 * {@link BoundedOutOfOrdernessTimestampExtractor} and {@link AscendingTimestampExtractor}
 */
public class CustomWatermarkExtractor implements AssignerWithPunctuatedWatermarks<EverestCollectorData> {

    private static final long serialVersionUID = 6677712640216480841L;
    private long currentTimestamp = Long.MIN_VALUE;

    @Override
    public long extractTimestamp(EverestCollectorData data, long previousElementTimestamp) {
        return data.localToTimeStamp();
    }

    @Nullable
    @Override
    public Watermark checkAndGetNextWatermark(EverestCollectorData lastElement, long extractedTimestamp) {
        return new Watermark(extractedTimestamp - 1500);
    }
}

