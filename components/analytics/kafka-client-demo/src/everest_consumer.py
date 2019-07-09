#!/usr/bin/env python
#
# File           : everest_consumer.py
# Description    : a python script implementing multithreaded kafka consumer
#                  - consume kafka message published by the analytics
#                  - act on incoming kafka message based on the rule engine
#                  
#
# Prerequisites  : 
#                  - Python
#                  - Python kafka client package
#                    % pip install kafka-python
#                  - Python kubernetes client
#                    % pip install kubernetes
#                  - kubernetes cluster
#                  - systemaccount privileges to operate on the kubernetes cluster
#
#
# REMARKS        :
#                  KAFKA_BOOTSTRAPPER environment will overwrite the default bootstrapper but -b takes the highest precedence
#          
# Date created   : 6/12/2019
# Python Version : 2.7 or 3.6
#                  

from __future__ import print_function  # python 2/3 compatibility


import os
import threading
import logging
import time
import json
from kafka.errors import KafkaError

from kafka import KafkaConsumer
import json
import time

class Consumer(threading.Thread):
    daemon = True

    def __init__(self, bootstrapper, kafka_topic, group_id, id=None):
        threading.Thread.__init__(self)
        self.bootstrapper = bootstrapper
        self.topic = kafka_topic
        self.gid = group_id
        self.consumer = None
        self.everest_k8s = None
        if id is None:
            self.id = kafka_topic
        else:
            self.id = id
        self.triggered = {}
 
    def register_k8s(self, k8s):
        self.everest_k8s = k8s

    def run(self):
        try:
            self.consumer = KafkaConsumer(self.topic, bootstrap_servers=self.bootstrapper,
                            group_id=self.gid,
                            enable_auto_commit=True, 
                            auto_offset_reset='latest')
                            #auto_offset_reset='earliest')
            print('Start Kafka Consumer to {}, topic {}, group id {} ready!'.format(self.bootstrapper, self.topic, self.gid))

            # self.consumer.poll() # dummy poll
            #self.consumer.seek_to_end()
            for message in self.consumer:
                # print(message.value)
                try:
                    bdata = message.value.decode('utf8')
                    data = json.loads(bdata)
                    
                    #print("Executing action callback")
                    pod_name = data['podName']
                    namespace = data['namespace']
                    val = data['value']
                    percent = data['percentage']
                    print("Thread ID {} RECEIVING EVENT ---> Value = {}".format(self.id, val))
                    t = time.time()
                    k = pod_name + "@" + namespace
                    self.everest_k8s.action(data=data)
                    print("Thread ID {} END Executing ACTION <--- \n".format(self.id))
                except Exception as e:
                    print("error: {0}".format(e))
        except KeyboardInterrupt:
            print('ERROR: Kafka Consumer, caused by: %s, thread ended', kafka_error.message)    
    
    def close(self):
        try:
            if self.consumer is not None:
                self.consumer.close()
                print('Kafka Consumer id {} closed'.format(self.id))
        except KafkaError as kafka_error:
            print('ERROR: Failed to close Kafka Consumer id {}, caused by: {}'.format(self.id, kafka_error.message))    