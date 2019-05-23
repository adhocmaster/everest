#!/bin/sh

ELIOT_INFLUXDB_HOST=""
ELIOT_INFLUXDB_PORT=""
INFLUXDB_HOST="influxdb-service.default.svc.cluster.local"
INFLUXDB_PORT="8086"

usage() {
	    echo "eliot-influx.sh [-h] [-l] [-host influxdb_host] [-port influxdb_port]"
	    echo "influxdb shell to connect to eliot influxdb"
	    echo "-h influxdb_host, default is $INFLUXDB_HOST"
	    echo "-p influxdb_port, default is $INFLUXDB_PORT"
}

for arg in "$@"
do
    case "$1" in
	-h)
	    usage
	    exit 0
	    ;;
	-host)
	    shift
	    ELIOT_INFLUXDB_HOST="-host $1"
	    shift
	    ;;
	-port)
	    shift
	    ELIOT_INFLUXDB_PORT="-port $1"
        shift
	    ;;
	*)
	    echo "Parameter error $1"
        usage
        exit 1
	    ;;
    esac
done
if [ "$ELIOT_INFLUXDB_HOST" = "" ]
then
    ELIOT_INFLUXDB_HOST="-host $INFLUXDB_HOST"
fi
if [ "$ELIOT_INFLUXDB_PORT" = "" ]
then
    ELIOT_INFLUXDB_PORT="-host $INFLUXDB_HOST"
fi

influx $ELIOT_INFLUXDB_HOST $ELIOT_INFLUXDB_PORT