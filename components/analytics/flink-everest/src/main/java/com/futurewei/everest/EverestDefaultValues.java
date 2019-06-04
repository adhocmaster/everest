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

package com.futurewei.everest;

public interface EverestDefaultValues {
    String PRJ_ID = "everest";
    String KAFKA_EVEREST_DATA_TOPIC = "everest-data-topic";
    String KAFKA_CG = "cg-" + PRJ_ID;
    String KAFKA_OUTPUT_CPU_C_TOPIC = "cpu-h-topic";
    String KAFKA_OUTPUT_CPU_H_TOPIC = "cpu-h-topic";
    String KAFKA_OUTPUT_MEM_C_TOPIC = "mem-c-topic";
    String KAFKA_OUTPUT_MEM_H_TOPIC = "mem-h-topic";
    String KAFKA_OUTPUT_NET_C_TOPIC = "net-c-topic";
    String KAFKA_OUTPUT_NET_H_TOPIC = "net-h-topic";
    Double VALID_VALUE_LOW_BOUND = 0.0;
    Double VALID_VALUE_HIGH_BOUND = 100.0;
    String CONCURENCY = "2";
    int CPU_THRESHOLD_CRITICAL = 90;
    int CPU_THRESHOLD_HIGH = 70;
    int CPU_THRESHOLD_REGULAR = 50;
    int MEM_THRESHOLD_CRITICAL = 90;
    int MEM_THRESHOLD_HIGH = 70;
    int MEM_THRESHOLD_REGULAR = 50;
    int NET_THRESHOLD_CRITICAL = 90;
    int NET_THRESHOLD_HIGH = 70;
    int NET_THRESHOLD_REGULAR = 50;

    /**
     * Type to select filters
     */
    String TYPE_TO_COLLECT_CPU = "cpu";
    String TYPE_TO_COLLECT_MEM = "mem";
    String TYPE_TO_COLLECT_NET = "net";

    /**
     * Category filters
     */
    String CATEGORY_CPU_CRITICAL = "cpu_critical";
    String CATEGORY_CPU_HIGH = "cpu_high";
    String CATEGORY_CPU_REGULAR = "cpu_regular";
    String CATEGORY_CPU_LOW = "cpu_low";
    String CATEGORY_MEM_CRITICAL = "mem_critical";
    String CATEGORY_MEM_HIGH = "mem_high";
    String CATEGORY_MEM_REGULAR = "mem_regular";
    String CATEGORY_MEM_LOW = "mem_low";
    String CATEGORY_NET_CRITICAL = "net_critical";
    String CATEGORY_NET_HIGH = "net_high";
    String CATEGORY_NET_REGULAR = "net_regular";
    String CATEGORY_NET_LOW = "net_low";

    /**
     * Prometheus related IDs
     */
    String VALID_CPU_COUNTER = "valid_cpu_counter";
    String INVALID_CPU_COUNTER = "invalid_cpu_counter";
    String VALID_MEM_COUNTER = "valid_mem_counter";
    String INVALID_MEM_COUNTER = "invalid_mem_counter";
    String VALID_NET_COUNTER = "valid_net_counter";
    String INVALID_NET_COUNTER = "invalid_net_counter";


    String EVEREST_METRICS_GROUP = "EverestMetrics";
    String CPU_CRITICAL_NUMBERS = "cpu_critical_numbers";
    String CPU_HIGH_NUMBERS = "cpu_high_numbers";
    String CPU_REGULAR_NUMBERS = "cpu_regular_numbers";
    String CPU_LOW_NUMBERS = "cpu_low_numbers";
    String NET_CRITICAL_NUMBERS = "net_critical_numbers";
    String NET_HIGH_NUMBERS = "net_high_numbers";
    String NET_REGULAR_NUMBERS = "net_regular_numbers";
    String NET_LOW_NUMBERS = "net_low_numbers";
    String MEM_CRITICAL_NUMBERS = "mem_critical_numbers";
    String MEM_HIGH_NUMBERS = "mem_high_numbers";
    String MEM_REGULAR_NUMBERS = "mem_regular_numbers";
    String MEM_LOW_NUMBERS = "mem_low_numbers";

    String CLUSTER_NUMBERS = "cluster_numbers";
    String POD_NUMBERS = "pod_numbers";
    String CPU_THROUGHPUT = "cpu_throughput";
    String MEM_THROUGHPUT = "mem_throughput";
    String NET_THROUGHPUT = "net_throughput";

    String BOOTSTRAP_SERVERS = "bootstrap.kafka:9092";
    //String BOOTSTRAP_SERVERS = "localhost:9092";
    String WINDOW_SIZE = "20"; //20 sec

}
