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


from kafka import KafkaConsumer
import json

class Consumer(threading.Thread):
    daemon = True

    def __init__(self, bootstrapper, kafka_topic, group_id):
        threading.Thread.__init__(self)
        self.bootstrapper = bootstrapper
        self.topic = kafka_topic
        self.gid = group_id
 
    def run(self):
        try:
            self.consumer = KafkaConsumer(self.topic, bootstrap_servers=self.bootstrapper,
                            enable_auto_commit=True, 
                            auto_offset_reset='earliest')
            print('Start Kafka Consumer to {}, topic {}, group id {} ready!'.format(self.bootstrapper, self.topic, self.gid))

            for message in self.consumer:
                # print(message.value)
                try:       
                    bdata = message.value.decode('utf8')
                    data = json.loads(bdata)
                    print("Topic {} RECEIVING ---> ".format(self.topic))
                    print(json.dumps(data, sort_keys=True, indent=4))
                    print("Topic {} End of RECEIVING <--- ".format(self.topic))
                except Exception as e:
                    print("error: {0}".format(e))
        except KeyboardInterrupt:
            print('ERROR: Kafka Consumer, caused by: %s, thread ended', kafka_error.message)    
    
    def close(self):
        try:
            self.consumer.close()
            print('Kafka Consumer closed')
        except KafkaError as kafka_error:
            print('ERROR: Failed to close Kafka Consumer, caused by: %s', kafka_error.message)    