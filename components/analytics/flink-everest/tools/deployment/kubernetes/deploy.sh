#!/bin/sh
#
# File          :  deploy.sh
# Description   :  A helper script to bootstrapper all kubernetes services + all stuffs for Eliot Flink Job
#
# Requires      : - Running kubernetes and kubectl
#                 - complete skylark repo (DON'T CHANGE the directory structure)
#

SKYLARK_ROOT=../../../..
SKYLARK_STUFFS="CASSANDRA MONITORING FLINK ELIOT_TOOLS"
SKYLARK_STUFF_DIRS=""
KC=kubectl
KUBECONFIG=""
OP="apply"

###
# BUG BUG BUG
# change this if you are NOT using vagrant
###
MYVAGRANT_ROOT=/vagrant
#VOLUME="docker"
VOLUME="local"

delete_pvc_kafka() {
    echo "---> Delete PVC for kafka and zookeeper (manually)"
    start=0
    end=2
    for index in $(seq $start $end)
    do
        $KC $KUBECONFIG delete pvc data-zoo-$index
        $KC $KUBECONFIG delete pvc data-kafka-$index
    done
}

build_shared_dir() {
    echo "---> Build Shared Directories on $MYVAGRANT_ROOT"

    start=0
    end=2
    for index in $(seq $start $end)
    do
        mkdir -p $MYVAGRANT_ROOT/exported/data-zoo-$index
        chmod -R ugo+rw $MYVAGRANT_ROOT/exported/data-zoo-$index
        mkdir -p $MYVAGRANT_ROOT/exported/data-kafka-$index
        chmod -R ugo+rw $MYVAGRANT_ROOT/exported/data-kafka-$index
    done
}

set_dirs() {
    SKYLARK_STUFF_DIRS="$SKYLARK_ROOT/kafka-clover/kafka-service $SKYLARK_ROOT/monitoring-eliot $SKYLARK_ROOT/cassandra-kubernetes $SKYLARK_ROOT/flink-kubernetes/session-cluster $SKYLARK_ROOT/flink-eliot/tools/deployment/kubernetes/eliottools"
    #SKYLARK_STUFF_DIRS="$SKYLARK_ROOT/kafka-clover/kafka-service $SKYLARK_ROOT/monitoring-eliot $SKYLARK_ROOT/flink-kubernetes/session-cluster/"
    #SKYLARK_STUFF_DIRS="$SKYLARK_ROOT/flink-kubernetes/session-cluster"
}

check_dirs() {
    #echo "SKYLARK_STUFF_DIRS $SKYLARK_STUFF_DIRS"
    for stuff_dir in $SKYLARK_STUFF_DIRS
    do
        if [ ! -d "$stuff_dir" ]
        then
            echo "Directory $stuff_dir does NOT exists, are you using complete skylark repo? Please clone the repo and restart again."
            exit 1
        fi
    done
}

install_it() {
    echo
    if [ "$OP" = "apply" ]
    then
        #build_shared_dir
        $KC $KUBECONFIG $OP -f $SKYLARK_ROOT/kafka-clover/kafka-storage/$VOLUME-storageclass-zookeeper.yml
        $KC $KUBECONFIG $OP -f $SKYLARK_ROOT/kafka-clover/kafka-storage/$VOLUME-storageclass-broker.yml
        $KC $KUBECONFIG $OP -f $SKYLARK_ROOT/kafka-clover/kafka-storage/$VOLUME-volume/
    fi
    for stuff_dir in $SKYLARK_STUFF_DIRS
    do
        if [ -d "$stuff_dir" ]
        then
            echo "---> $KC $OP -R -f $stuff_dir"
            $KC $KUBECONFIG $OP -R -f $stuff_dir
        fi
    done
    if [ "$OP" = "delete" ]
    then
        sleep 20
        delete_pvc_kafka
        echo "---> $KC $KUBECONFIG $OP -f $SKYLARK_ROOT/kafka-clover/kafka-storage/$VOLUME-volume/"
        $KC $KUBECONFIG $OP -f $SKYLARK_ROOT/kafka-clover/kafka-storage/$VOLUME-volume/
        echo "---> $KC $KUBECONFIG $OP -f $SKYLARK_ROOT/kafka-clover/kafka-storage/$VOLUME-storageclass-broker.yml"
        $KC $KUBECONFIG $OP -f $SKYLARK_ROOT/kafka-clover/kafka-storage/$VOLUME-storageclass-broker.yml
        echo "---> $KC $KUBECONFIG $OP -f $SKYLARK_ROOT/kafka-clover/kafka-storage/$VOLUME-storageclass-zookeeper.yml"
        $KC $KUBECONFIG $OP -f $SKYLARK_ROOT/kafka-clover/kafka-storage/$VOLUME-storageclass-zookeeper.yml
#        echo "---> rm -rf $MYVAGRANT_ROOT/exported"
#        rm -rf $MYVAGRANT_ROOT/exported
    fi

}

for arg in "$@"
do
    case "$1" in
	-h)
	    echo "deploy.sh [-h] [-d] [-root root_dir] [-k kube-config-file]"
	    echo "deploy or undeploy all skylark eliot related kubernetes artifacts"
	    echo "-d undeploy all skylark eliot related kubernetes artifacts, by default it is going to deploy"
	    echo "-root root_dir, default is $SKYLARK_ROOT"
	    exit
	    ;;
	-root)
	    shift
	    SKYLARK_ROOT="$1"
	    shift
	    ;;
	-d)
	    OP="delete"
	    shift
	    ;;
	-k | --kubeconfig)
	    shift
	    KUBECONFIG="--kubeconfig $1"
	    shift
	    ;;
    esac
done

set_dirs
check_dirs
echo
echo "Start (de)installing Kubernetes artifacts for Eliot Job with SKYLARK_ROOT = $SKYLARK_ROOT"
install_it
