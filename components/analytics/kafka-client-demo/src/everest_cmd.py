#!/usr/bin/env python
#
# File           : everest_cmd.py
# Description    : a python script implementing command and control for everest
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
# How to run     :
#                  % python everest_cmd.py
#
#                  To run it against broker at address 'KAFKA_BROKER_URL'
#                  '-b':
#                  % python everest_cmd.py -b KAFKA_BROKER_URL
# 
#                  To run it against broker at address 'KAFKA_BROKER_URL' and publish it into a topic 'MYTOPIC'
#                  use '-b' and '-t':
#                  % python everest_cmd.py -b KAFKA_BROKER_URL -t MYTOPIC
# 
#                  To get all options run the program with '-h':
#                  % python everest_cmd.py -h
#
# REMARKS        :
#                  KAFKA_BOOTSTRAPPER environment will overwrite the default bootstrapper but -b takes the highest precedence
#          
# Date created   : 6/12/2019
# Python Version : 2.7 or 3.6
#                  

from __future__ import print_function  # python 2/3 compatibility

import os
import sys # used to exit
from everest_consumer import Consumer
from kube import EverestK8s

import atexit

consumers = []
def shutdown_hook():
    """
    a shutdown hook to be called before the shutdown
    :param consumer: instance of a kafka consumer
    :param session: instance of a cassandra session
    :return: None
    """
    print('Closing All Consumers')
    for consumer in consumers:
        consumer.close()
    print('All Consumer closed')

def main(bootstrapper, kafka_topic=[], group_id='everest-c-group', outside=False):
    threads = []
    everest_k8s = EverestK8s(incluster=False)
    for topic in kafka_topic:
        c = Consumer(bootstrapper, topic, group_id)
        c.register_k8s(everest_k8s)
        consumers.append(c)
        threads.append(c)
        c.start()
    for t in threads:
        t.join()

if __name__ == "__main__":
    #BOOTSTRAPPER='bootstrap.kafka.svc.cluster.local:9092'
    #BOOTSTRAPPER='master:32400'
    BOOTSTRAPPER='localhost:9092'
    TOPICS=''
    TOPIC=[]
    GROUP_ID='everest-c-group'
    OUTSIDE=False
    VERBOSE=False

    import argparse
    parser = argparse.ArgumentParser()
    parser.add_argument("-b", "--bootstrapper", help="kafka bootstrapper address, default is {0}".format(BOOTSTRAPPER), default=BOOTSTRAPPER)
    parser.add_argument("-t", "--topics", help="topic to listened to, default is {0}".format(TOPICS), default=TOPICS)
    parser.add_argument("-g", "--group", help="Kafka Group ID, default is {0}".format(GROUP_ID), default=GROUP_ID)
    parser.add_argument("-v", "--verbose", help="print out the logging info, default is {0}".format(VERBOSE), default=VERBOSE, action="store_true")
    parser.add_argument("-o", "--outside", help="run the program outside k8s cluster {0}".format(OUTSIDE), default=OUTSIDE, action="store_true")
   
    args = parser.parse_args()

    # import logging
    # logging.basicConfig(
    #     format='%(asctime)s.%(msecs)s:%(name)s:%(thread)d:' +
    #            '%(levelname)s:%(process)d:%(message)s',
    #     level=logging.DEBUG
    # )

    if 'EVEREST_CMD_BOOTSTRAPPER' in os.environ:
        BOOTSTRAPPER = os.environ['EVEREST_CMD_BOOTSTRAPPER']
    if 'EVEREST_CMD_TOPICS' in os.environ:
        TOPICS = os.environ['EVEREST_CMD_TOPICS']
    if 'EVEREST_CMD_TOPICS_GRP' in os.environ:
        GROUP_ID = os.environ['EVEREST_CMD_TOPICS_GRP']
    if 'EVEREST_CMD_OUTSIDE' in os.environ:
        OUTSIDE = os.environ['EVEREST_CMD_OUTSIDE']

    if args.bootstrapper != '':
        BOOTSTRAPPER = args.bootstrapper
    if args.topics != '':
        TOPICS = args.topics
    if args.group != '':
        GROUP_ID = args.group
    if args.outside is True:
        OUTSIDE = args.outside
    
    TOPIC = TOPICS.split(",")

    atexit.register(shutdown_hook)

    main(BOOTSTRAPPER, TOPIC, GROUP_ID, OUTSIDE)
