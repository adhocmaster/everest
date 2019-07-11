#!/bin/sh

SINKS=""
TOPIC=""
BOOTSTRAPPER=""
KUBE_BG=""
K8S_PORT=8080
KUBECTL=kubectl

usage() {
	    echo "everest_cmd.sh [-h] [-k proxy_port] [-s] [-t topic] [-b kafka_bootstrapper]"
	    echo "script to start container for everest analytics"
		echo "-h						: print this help"
		echo "-s						: to start with the flink sink on kafka"
		echo "-t topic					: connect to kafka 'topic' as the sink"
		echo "-b kafka_bootstrapper		: connect to kafka at kafka_bootstrapper"
		echo "-k proxy_port				: start with kubectl proxy as a sidecar/background process listening at proxy_port, default '$K8S_PORT'"
		echo 
}
# echo "-$#-"
for arg in "$@"
do
    #echo "arg -$1-"
    case "$1" in
    "")
        shift
        ;;
	-h)
	    usage
	    exit 0
	    ;;
	-s)
		SINKS="true"
	    shift
	    ;;	
	-k)
		KUBE_BG="true"
	    shift
		K8S_PORT="$1"
		shift
	    ;;
	-t)
	    shift
	    TOPIC="$1"
		shift
	    ;;
	-b)
	    shift
	    BOOTSTRAPPER="$1"
		shift
	    ;;
	*)
	    echo "Parameter error -$1-"
	    shift
        usage
        exit 1
	    ;;
    esac
done

if [ "$KUBE_BG" != "" ]
then
	echo "Start kubectl proxy on port $K8S_PORT"
	echo "$KUBECTL proxy --port=$K8S_PORT &"
	$KUBECTL proxy --port=$K8S_PORT &
fi

if [ "$SINKS" != "" ]
then
	echo "Start the container python entry point"
	if [ "$TOPIC" = "" ]
	then
		usage
		exit 1
	fi
	echo "(cd /app/pipeline/sinks; python everest_cmd.py -b $BOOTSTRAPPER -t $TOPIC)"
	(cd /app/pipeline/sinks; python -u everest_cmd.py -b $BOOTSTRAPPER -t $TOPIC)
	echo "Everest Sinks started and the Tools is DONE and READY..."
else
	echo "Start the container cmd entry point"
	echo "Everest Tools is DONE and READY..."
	tail -f /dev/null
fi

