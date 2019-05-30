#!/usr/bin/env python
#
# File           : kafka-demo-consumer.py
# Description    : a python script to consume kafka message published by a producer
#
# Prerequisites  : 
#                  - Python
#                  - Python kafka client package
#                    % pip install kafka-python
# How to run     :
#                  % python kafka-demo-consumer.py
#
#                  To run it against broker at address 'KAFKA_BROKER_URL'
#                  '-b':
#                  % python kafka-demo-consumer.py -b KAFKA_BROKER_URL
# 
#                  To run it against broker at address 'KAFKA_BROKER_URL' and publish it into a topic 'MYTOPIC'
#                  use '-b' and '-t':
#                  % python kafka-demo-consumer.py -b KAFKA_BROKER_URL -t MYTOPIC
# 
#                  To get all options run the program with '-h':
#                  % python kafka-demo-consumer.py -h
#
# REMARKS        :
#                  KAFKA_BOOTSTRAPPER environment will overwrite the default bootstrapper but -b takes the highest precedence
#          
# Date created   : 8/8/2018
# Python Version : 2.7 or 3.6
#                  

from __future__ import print_function  # python 2/3 compatibility

import os
import sys # used to exit
from kafka import KafkaConsumer
import json

def main(bootstrapper, kafka_topic, group_id, _json=False):
    print('Start Kafka Consumer to {}, topic {}, group id {}'.format(bootstrapper, kafka_topic, group_id))

    #group_id=group_id, 
    consumer = KafkaConsumer(kafka_topic, bootstrap_servers=bootstrapper,
                             enable_auto_commit=True, 
                         auto_offset_reset='earliest')
    try:
        print("Consumer with bootstrapper {} on topic {} ready!".format(bootstrapper, kafka_topic))
        for message in consumer:
            # print(message.value)
            try:       
                #print(bdata)
                if _json is True:
                    bdata = message.value.decode('utf8')
                    data = json.loads(bdata)
                    for key, value in data.items() :
                        print ('KEY: {} VALUE: {}'.format(key, value))
                else:
                    print(message.value)
 
                #print("VALUE " + str(data['top']))
            except Exception as e:
                print("error: {0}".format(e))
    except KeyboardInterrupt:
        sys.exit()

if __name__ == "__main__":
    #BOOTSTRAPPER='bootstrap.kafka.svc.cluster.local:9092'
    BOOTSTRAPPER='master:32400'
    #BOOTSTRAPPER='localhost:9092'
    TOPIC='l-topic'
    GROUP_ID='someid'
    #TOPIC='mytest'

    import argparse
    parser = argparse.ArgumentParser()
    parser.add_argument("-b", "--bootstrapper", help="kafka bootstrapper address, default is {0}".format(BOOTSTRAPPER), default=BOOTSTRAPPER)
    parser.add_argument("-t", "--topic", help="topic to create and on which the messages are published, default is {0}".format(TOPIC), default=TOPIC)
    parser.add_argument("-g", "--group", help="Kafka Group ID, default is {0}".format(GROUP_ID), default=GROUP_ID)
    parser.add_argument("-j", "--json", help="send json formatted values", action='store_true')
    
    args = parser.parse_args()

    # import logging
    # logging.basicConfig(
    #     format='%(asctime)s.%(msecs)s:%(name)s:%(thread)d:' +
    #            '%(levelname)s:%(process)d:%(message)s',
    #     level=logging.DEBUG
    # )

    if 'KAFKA_BOOTSTRAPPER' in os.environ:
        BOOTSTRAPPER = os.environ['KAFKA_BOOTSTRAPPER']

    if args.bootstrapper != '':
        BOOTSTRAPPER = args.bootstrapper
    if args.topic != '':
        TOPIC = args.topic
    if args.group != '':
        GROUP_ID = args.group

    main(BOOTSTRAPPER, TOPIC, GROUP_ID, args.json)
