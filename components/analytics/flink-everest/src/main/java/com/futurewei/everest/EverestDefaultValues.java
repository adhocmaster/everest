/*
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
 */

package com.futurewei.everest;

public interface EverestDefaultValues {
    String PRJ_ID = "everest";
    String KAFKA_EVEREST_DATA_TOPIC = "everest-data-topic";
    String KAFKA_CG = "cg-" + PRJ_ID;
    String KAFKA_OUTPUT_LOW_TOPIC = "l-topic";
    String KAFKA_OUTPUT_REGULAR_TOPIC = "r-topic";
    String KAFKA_OUTPUT_HIGH_TOPIC = "h-topic";
    String KAFKA_OUTPUT_CRITICAL_TOPIC = "c-topic";
    Double VALID_VALUE_LOW_BOUND = 0.0;
    Double VALID_VALUE_HIGH_BOUND = 100.0;
    int THRESHOLD_HIGH = 20;
    int THRESHOLD_CRITICAL = 10;
    String BOOTSTRAP_SERVERS = "bootstrap.kafka.svc.cluster.local:9092";
    String WINDOW_SIZE = "5"; //sec

    /*
        Influxdb constants
     */
//    String INFLUXDB_DATABASENAME = "eliot_db";
//    String INFLUXDB_INFLUXDBURL = "http://influxdb-service." + K_NAMESPACE + ".svc.cluster.local:8086";
//    //String INFLUXDB_INFLUXDBURL = "http://localhost:8086";
//    String INFLUXDB_INFLUXDBUSER = "admin";
//    String INFLUXDB_INFLUXDBPWD = "admin";
//    String INFLUXDB_FIELDABSDIFF = "absdiff";
//    String INFLUXDB_FIELDVALUE = "value";
//    String INFLUXDB_FIELDVALUE0 = "value0";
//    String INFLUXDB_FIELDTS = "orgTS";
//    String INFLUXDB_SENSORID = "sensorid";
//
    /*
        Cassandra Constants
     */
//    String CASSANDRA_USED="true";
//    //String CASSANDRA_HOST="localhost";
//    String CASSANDRA_HOST="cassandra-service." + K_NAMESPACE + ".svc.cluster.local";
//    String CASSANDRA_PORT_NOTUSED="-1";
//    String CASSANDRA_ELIOT_KEYSPACE="eliot_keyspace";
//    String CASSANDRA_ELIOT_TABLE="sensors";
//    String CASSANDRA_POLL_INTERVAL = "3"; // 3 minutes
}
