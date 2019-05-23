#!/bin/sh

GATEWAY_NUMBER=2
SENSOR_NUMBER=50
GATEWAY_PREFIX="ST"
SENSOR_PREFIX="SID"

usage() {
	    echo "entrypoint.sh [-h] [-l] [-g gateway_number] [-s sensor_number] [-gp gateway_prefix] [-sp sensor_prefix]"
	    echo "script to create registration data"
	    echo "-g gateway_number, default is $GATEWAY_NUMBER"
	    echo "-s sensor_number, default is $SENSOR_NUMBER"
	    echo "-gp gateway_prefix, default is $GATEWAY_PREFIX"
	    echo "-sp sensor_prefix, default is $SENSOR_PREFIX"
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
	-g)
	    shift
	    GATEWAY_NUMBER="$1"
	    shift
	    ;;
	-s)
	    shift
	    SENSOR_NUMBER="$1"
        shift
	    ;;
	-gp)
	    shift
	    GATEWAY_PREFIX="$1"
	    shift
	    ;;
	-sp)
	    shift
	    SENSOR_PREFIX="$1"
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

#(cd /app/visualization; ./grafana_datasource.sh; ./grafana_datasource.sh -json kafka_datasource.json; ./eliot_dashboard.sh; ./eliot_dashboard.sh -json kafka_overview_dashboard.json; ./eliot_dashboard.sh -json kafka_exporter_dashboard.json; ./eliot_dashboard.sh -json flink_metric_dashboard.json)
echo "Start the container entry point"

iter="A B C D E"
MINUS_N=""

for i in $iter
do
    if [ "$i" != "A" ]
    then
        MINUS_N="-n"
    fi
    gp="$GATEWAY_PREFIX""_""$i"
    sp="$SENSOR_PREFIX""_""$i"
    echo "Executing (cd /app/sensors/sim/src; python registry_app.py -g $GATEWAY_NUMBER -s $SENSOR_NUMBER -gp $gp -sp $sp $MINUS_N)"
    (cd /app/sensors/sim/src; python registry_app.py -g $GATEWAY_NUMBER -s $SENSOR_NUMBER -gp $gp -sp $sp $MINUS_N)
done

echo "Eliot Tools is DONE and READY..."

tail -f /dev/null