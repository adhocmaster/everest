#!/bin/sh

ELIOT_KAFKA_HOST="bootstrap.default.svc.cluster.local"
ELIOT_KAFKA_PORT=9092
ELIOT_KAFKA_TOPIC="in-topic"
OP="producer.py"
OP_DIR="/app/sensors/sim/src"

usage() {
	    echo "eliot-kafka.sh [-h] [-c|-p] [-h kafkahost] [-p kafkaport] [-i topic] "
	    echo "kafka client to connect to eliot kafka"
	    echo "-c|-p producer or consumer, -p is producer, -c is consumer, default producer"
	    echo "-h kafkahost, default is $ELIOT_KAFKA_HOST"
	    echo "-p kafkaport, default is $ELIOT_KAFKA_PORT"
	    echo "-i topic, default is $ELIOT_KAFKA_TOPIC"
}

for arg in "$@"
do
    case "$1" in
	-h)
	    usage
	    exit 0
	    ;;
	-p)
	    OP="producer.py"
	    OP_DIR="/app/sensors/sim/src"
	    shift
	    ;;
	-c)
	    OP="consumer.py"
	    OP_DIR="/app/sensors/sim/src/receiver/kafka"
	    shift
	    ;;
	-h)
	    shift
	    ELIOT_KAFKA_HOST="$1"
        shift
	    ;;
	-p)
	    shift
	    ELIOT_KAFKA_PORT="$1"
        shift
	    ;;
	-i)
	    shift
	    ELIOT_KAFKA_TOPIC="$1"
        shift
	    ;;
	*)
	    echo "Parameter error $1"
        usage
        exit 1
	    ;;
    esac
done

(cd $OP_DIR; python $OP -i $ELIOT_KAFKA_TOPIC -k $ELIOT_KAFKA_HOST:$ELIOT_KAFKA_PORT)