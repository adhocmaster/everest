#!/usr/bin/env python
#
# File           : kafka_client.py
# Description    : a python script to run multithreaded consumer and producer on Kafka
#
# Prerequisites  : 
#                  - Python
#                  - Python kafka client package
#                    % pip install kafka-python
# How to run     :
#                  To run simple producer with 1 topics and 1000 messages in 10 iteration
#                  '-i':
#                  % python kafka_client.py -i 10
#
#                  To get all options run the program with '-h':
#                  % python kafka_client.py -h
#
# REMARKS        :
#          
# Date created   : 8/6/2018
# Python Version : 2.7
#                  

import os
import threading
import logging
import time
import json
from kafka import KafkaConsumer, KafkaProducer

KAFKA_TOPIC="kafka-client-topic"
class Producer(threading.Thread):
    daemon = True
    
    def __init__(self, bootstrapper, iter, num_messages):
        self.bootstrapper = bootstrapper
        self.iter = iter
        self.num_messages = num_messages
        print("Init Producer with bootstrapper: {}, iteration = {}, number of messages {} ".format(self.bootstrapper, iter, num_messages))

    def run(self):
        producer = KafkaProducer(bootstrap_servers=self.bootstrapper,
                                 value_serializer=lambda v: json.dumps(v).encode('utf-8'))
        counter = 0
        while counter >= self.iter:
            for i in range(self.num_messages):
                producer.send(KAFKA_TOPIC, {"dataObjectID": "Message Nr. {}. test1 sent by kafka_client".format(i)})
                producer.send(KAFKA_TOPIC, {"dataObjectID": "Message Nr. {}. test2 sent by kafka client".format(i)})
                producer.send(KAFKA_TOPIC, {"dataObjectID": "Message Nr. {}. test3 sent by kafka client".format(i)})
            counter += 1
            time.sleep(1)

class Consumer(threading.Thread):
    daemon = True

    def __init__(self, bootstrapper):
        self.bootstrapper = bootstrapper
        print("Init Consumer with bootstrapper: {}".format(self.bootstrapper))

    def run(self):
        consumer = KafkaConsumer(bootstrap_servers=self.bootstrapper,
                                 auto_offset_reset='earliest',
                                 value_deserializer=lambda m: json.loads(m.decode('utf-8')))
        consumer.subscribe([KAFKA_TOPIC])
        for message in consumer:
            print ("Kafka Client Consumer received <--- {}".format(message))

def main(bootstrapper, iter, num_messages):
     threads = [
         Producer(bootstrapper, iter, num_messages),
         Consumer(bootstrapper)
     ]
     for t in threads:
         t.start()
     for t in threads:
         t.join()


if __name__ == "__main__":
    BOOTSTRAPPER='bootstrap.kafka.svc.cluster.local:9092'
    VERBOSE=False
    ITER=1
    NUM_MESSAGES=1000

    import argparse
    parser = argparse.ArgumentParser()
    parser.add_argument("-b", "--bootstrapper", help="kafka bootstrapper address, default is {0}".format(BOOTSTRAPPER), default=BOOTSTRAPPER)
    
    parser.add_argument("-v", "--verbose", help="print out the logging info, default is {0}".format(VERBOSE), default=VERBOSE, action="store_true")
    parser.add_argument("-i", "--iter", help="number of iteration, default is {0}".format(ITER), default=ITER)
    parser.add_argument("-m", "--num_messages", help="number of messages per iteration, default is {0}".format(NUM_MESSAGES), default=NUM_MESSAGES)


    if 'KAFKA_BOOTSTRAPPER' in os.environ:
        BOOTSTRAPPER = os.environ['KAFKA_CLIENT_BOOTSTRAPPER']
    if 'KAFKA_CLIENT_VERBOSE' in os.environ:
        VERBOSE = os.environ['KAFKA__CLIENT_VERBOSE']
    if 'KAFKA_CLIENT_ITER' in os.environ:
        ITER = os.environ['KAFKA__CLIENT_ITER']
    if 'KAFKA_CLIENT_NUM_MESSAGES' in os.environ:
        NUM_MESSAGES = os.environ['KAFKA__CLIENT_NUM_MESSAGES']
    
    args = parser.parse_args()
    if args.verbose is True:
        VERBOSE = True
    if VERBOSE is True:
        logging.basicConfig(
            format='%(asctime)s.%(msecs)s:%(name)s:%(thread)d:' +
               '%(levelname)s:%(process)d:%(message)s',
            level=logging.DEBUG
            )

    if args.bootstrapper != '':
        BOOTSTRAPPER = args.bootstrapper
    if args.iter != '':
        ITER = args.iter
    if args.num_messages != '':
        NUM_MESSAGES = args.num_messages

    main(BOOTSTRAPPER, ITER, NUM_MESSAGES)
        
