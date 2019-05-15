#!/usr/bin/env python
#
# File           : kafka-demo-producer.py
# Description    : a python script to produce random text into Kafka
#
# Prerequisites  : 
#                  - Python
#                  - Python kafka client package
#                    % pip install kafka-python
# How to run     :
#                  % python kafka-demo-producer.py
#
#                  To run it against broker at address 'KAFKA_BROKER_URL'
#                  '-b':
#                  % python kafka-demo-producer.py -b KAFKA_BROKER_URL
# 
#                  To run it against broker at address 'KAFKA_BROKER_URL' and publish it into a topic 'MYTOPIC'
#                  use '-b' and '-t':
#                  % python kafka-demo-producer.py -b KAFKA_BROKER_URL -t MYTOPIC
# 
#                  To get all options run the program with '-h':
#                  % python kafka-demo-producer.py -h
#
# REMARKS        :
#                  KAFKA_BOOTSTRAPPER environment will overwrite the default bootstrapper but -b takes the highest precedence
#          
# Date created   : 8/8/2018
# Python Version : 2.7 or 3.6
#                  

from __future__ import print_function  # python 2/3 compatibility

import os
import time
import datetime
from random import randint

from kafka import KafkaProducer

SLEEP_TIME=5
ITERATION=1

def main(bootstrapper, kafka_topic, sleep_time=SLEEP_TIME, iteration=ITERATION, invalid=False):
    print('Start Kafka Producer to {}, topic {}'.format(bootstrapper, kafka_topic))
    
    producer = KafkaProducer(bootstrap_servers=bootstrapper)
    # Must send bytes
    messages = []
    dusages = []
    # Send the messages
    for i in range(100):
        now = datetime.datetime.now()
        ts_millisec = now.timestamp() * 1000
        dusages.append(-1) if invalid is True else dusages.append(0)
        messages.append(str.encode('ID{},{},{},DESCRIPTION{}'.format(i, dusages[i], int(ts_millisec), i)))
    for m in messages:
        print('Sending {}'.format(m))
        producer.send(kafka_topic, m)
    producer.flush()

    counter = 0
    while iteration == -1 or iteration > counter:
        print('Iteration Nr. {}'.format(counter))
        for i in range(100):
            dusages[i] = randint(0, 100) if invalid is False else randint(0, 100) * -1
            now = datetime.datetime.now()
            ts_millisec = now.timestamp() * 1000
            messages.append(str.encode('ID{},{},{},DESCRIPTION{}'.format(i, dusages[i], int(ts_millisec), i)))
            if sleep_time > 0:
                time.sleep(sleep_time/1000.0)
        for m in messages:
            print('Sending Again {}'.format(m))
            producer.send(kafka_topic, m)
        producer.flush()
        counter += 1
        
    print("Producer with bootstrapper {} send {} done!".format(bootstrapper, kafka_topic))

if __name__ == "__main__":
    #BOOTSTRAPPER='bootstrap.kafka.svc.cluster.local:9092'
    BOOTSTRAPPER='master:32400'
    #BOOTSTRAPPER='localhost:9092'
    TOPIC='in-topic'
    #TOPIC='mytest'
    import argparse
    parser = argparse.ArgumentParser()
    parser.add_argument("-b", "--bootstrapper", help="kafka bootstrapper address, default is {0}".format(BOOTSTRAPPER), default=BOOTSTRAPPER)
    parser.add_argument("-t", "--topic", help="topic to create and on which the messages are published, default is {0}".format(TOPIC), default=TOPIC)
    parser.add_argument("-d", "--delay", help="delay sending the data in milli second, default is {0}".format(SLEEP_TIME), default=SLEEP_TIME)
    parser.add_argument("-i", "--iter", help="how many time to send bulk data, default is {0}".format(ITERATION), default=ITERATION)
    parser.add_argument("-z", "--invalid", help="send only disk usage with negative values", action='store_true')
    
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
    if args.delay != '':
        SLEEP_TIME = float(args.delay)
    if args.iter != '':
        ITERATION = int(args.iter)

    main(BOOTSTRAPPER, TOPIC, SLEEP_TIME, ITERATION, args.invalid)
