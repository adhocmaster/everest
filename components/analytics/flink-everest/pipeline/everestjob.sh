#!/bin/sh
#
# Script to send job image to flink jobmanager
# Requires:
#      - Flink CLI
#      - Flink Cluster
#      - Flink jobmanager URL (or default to flink-jobmanager.default.svc.cluster.local:8081 defined at env EVEREST_FLINK_JOBMANAGER_HOST
#
#
FLINK_JOBMANAGER_HOST=""
if [ "$EVEREST_FLINK_JOBMANAGER_HOST" != "" ]
then
    FLINK_JOBMANAGER_HOST="$EVEREST_FLINK_JOBMANAGER_HOST"
fi
FLINK_JOB_JAR="flink-everest-1.0-SNAPSHOT.jar"
FLINK=flink
FLINK_JOB_CLASS=""
FLINK_CMD="-h"
FLINK_OP="exec"
FLINK_JOB_ID=""

run_it() {
    fjm_url=""
    fjj="$FLINK_JOB_JAR"
    fjc=""
    if [ "$FLINK_JOBMANAGER_HOST" != "" ]
    then
        fjm_url="-m $FLINK_JOBMANAGER_HOST"
    fi
    if [ "$FLINK_JOB_CLASS" != "" ]
    then
        fjc="-c $FLINK_JOB_CLASS"
    fi
    case "$FLINK_OP" in
    exec)
        $FLINK run $fjm_url $fjc $fjj
    ;;
    list)
        $FLINK list $fjm_url $fjc $fjj
    ;;
    cancel)
        $FLINK cancel $FLINK_JOB_ID $fjm_url
    ;;
    esac
}

for arg in "$@"
do
    case "$1" in
	-h)
	    echo "everestjob.sh [-h] [-l] [-c flink_job_id] [-m flink_job_manager_url] [-j flink_job_jar_file] [-e main_class_to_execute]"
	    echo "deploy EVEREST job on flink cluster"
	    echo "-m flink_job_manager_url, default is $EVEREST_FLINK_JOBMANAGER_HOST"
	    echo "-j flink_job_jar_file, default is $FLINK_JOB_JAR"
	    echo "-l list all, flink jobs"
	    echo "-c flink_job_id, cancel flink job with the id flink_job_id (use -l to find out the id)"
	    echo -e "-e main_class_to_execute, this has to adhere java class naming convention defined in flink-EVEREST (check the jar file if needed), \ndefault is $FLINK_JOB_CLASS"
	    exit 0
	    ;;
	-m)
	    shift
	    FLINK_JOBMANAGER_HOST="$1"
	    shift
	    ;;
	-l)
	    FLINK_OP="list"
        shift
	    ;;
	-c)
	    shift
	    FLINK_JOB_ID="$1"
	    FLINK_OP="cancel"
        shift
	    ;;
	-j)
	    shift
	    FLINK_JOB_JAR="$1"
	    shift
	    ;;
	-e)
	    shift
	    FLINK_JOB_CLASS="$1"
	    shift
	    ;;
    esac
done


run_it