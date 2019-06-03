#!/bin/sh

SINKS=""
TOPIC=""
usage() {
	    echo "everest_cmd.sh [-h] [-l] [-s] [-t topic]"
	    echo "script to start container for everest analytics"
}
echo "-$#-"
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
	-t)
	    shift
	    TOPIC="$1"
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

if [ "$SINKS" != "" ]
then
	echo "Start the container python entry point"
	echo "(cd sinks; python kafka-demo-consumer.py -g mygroup --json -t $TOPIC)"
	if [ "$TOPIC" == "" ]
	then
		usage
		exit 1
	fi
	(cd sinks; python kafka-demo-consumer.py -g mygroup --json -t $TOPIC)
else
	echo "Start the container cmd entry point"
	echo "Everest Tools is DONE and READY..."
	tail -f /dev/null
fi

