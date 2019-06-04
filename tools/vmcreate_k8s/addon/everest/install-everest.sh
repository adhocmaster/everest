#!/bin/sh
#
# Copyright 2018-2019 The Everest Authors
#
# Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
# in compliance with the License. You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software distributed under the License
# is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
# or implied. See the License for the specific language governing permissions and limitations under
# the License.
#
# Assumptions:
#     - Kubernetes is running
#     - istio 1.x.x is installed
#     - kubeconfig is correct
#
#
# Requires: 
#     - sudo
#     - kubeconfig
#     - kubectl
#     - make sure that your kube config file point to the RIGHT k8s cluster
#
#

#
# CLOUD_TYPE=vm
# adjust this if you are in different environment, alternatives are:
# gke, aws
#
CLOUD_TYPE=vm

OP=$1
if [ "$OP" = "-d" ]
then
    KUBE_CREATE="delete"
    KUBE_APPLY="delete"
else
    KUBE_CREATE="create"
    KUBE_APPLY="apply"
fi

ORG_DIR=`pwd`
# pushd /tmp
cd /tmp

#
# WARNING WARNING WARNING: be careful here
# don't do this if you are INSIDE THE VM
# do this if you are outside the VM
#
if [ "$KUBECONFIG" = "" ] && [ "$CLOUD_TYPE" = "vm" ]
then
    export KUBECONFIG=$HOME/kubeconfig.yml
fi

if [ -d everest ]
then
    rm -rf everest
fi

echo "Retrieve Everest software by git"
sudo apt-get install git
git clone https://github.com/iharijono/everest.git

#
# Let's patch original istio-system first
#
if [ "$KUBE_CREATE" = "create" ] && [ "$CLOUD_TYPE" = "vm" ]
then
    echo "Patching istio-ingressgateway: patch svc istio-ingressgateway -n istio-system -p '{spec:{type: NodePort}}'"
    kubectl patch svc istio-ingressgateway -n istio-system -p '{"spec":{"type": "NodePort"}}'
fi

#
#
# EVEREST ANALYTICS
#
#  WARNING WARNING WARNING: adjust namespace in kafka-service/00rbac-namespace-default/10-node-reader.yml
#

KAFKA_NAMESPACE="kafka"
KAFKA_NAMESPACE_N="-n $KAFKA_NAMESPACE"
KAFKA_DIR="./everest/deployment/kubernetes/vm/analytics/kafka-service"

if [ "$KAFKA_NAMESPACE" != "default" ]
then
    kubectl $KUBE_CREATE namespace $KAFKA_NAMESPACE
fi
if [ "$KUBE_CREATE" = "create" ]
then
    echo "Installing Kafka ..."
    kubectl $KUBE_APPLY -R -f $KAFKA_DIR $KAFKA_NAMESPACE_N
    echo "DONE Installing Kafka, wait ..."
    sleep 120
fi

#
#
# EVEREST APP SAMPLE
#
#

EVEREST_APP_DIR="./everest/deployment/kubernetes/vm/app"
EVEREST_APP_NAMESPACE="everest-app"
EVEREST_APP_NAMESPACE_N="-n $EVEREST_APP_NAMESPACE"
if [ "$EVEREST_APP_NAMESPACE" != "default" ]
then
    kubectl $KUBE_CREATE namespace $EVEREST_APP_NAMESPACE
fi
if [ "$KUBE_CREATE" = "create" ]
then
    echo "Install Everest Sample Apps"
    kubectl label namespace $EVEREST_APP_NAMESPACE istio-injection=enabled
    (cd $EVEREST_APP_DIR; kubectl apply -f file-server/file-server.yaml $EVEREST_APP_NAMESPACE_N)
    (cd $EVEREST_APP_DIR; kubectl apply -f file-server/fs-istio-gateway.yaml $EVEREST_APP_NAMESPACE_N)
    (cd $EVEREST_APP_DIR; kubectl apply -f file-server/fs-istio-destinationrules.yaml $EVEREST_APP_NAMESPACE_N)
    (cd $EVEREST_APP_DIR; kubectl apply -f guide/guide-server.yaml $EVEREST_APP_NAMESPACE_N)
    (cd $EVEREST_APP_DIR; kubectl apply -f guide/guide-istio-gateway.yaml $EVEREST_APP_NAMESPACE_N)
    (cd $EVEREST_APP_DIR; kubectl apply -f guide/guide-istio-destinationrules.yaml $EVEREST_APP_NAMESPACE_N)
    echo "DONE Installing Everest Sample Apps, wait ..."
    sleep 10
fi

EVEREST_NAMESPACE="everest"
EVEREST_NAMESPACE_N="-n $EVEREST_NAMESPACE"
if [ "$EVEREST_APP_NAMESPACE" != "default" ]
then
    kubectl $KUBE_CREATE namespace $EVEREST_NAMESPACE
fi

if [ "$KUBE_CREATE" = "create" ]
then
    echo "Install Everest ..."
    #
    # EVEREST services
    #
    EVEREST_SERVICES_DIR="./everest/deployment/kubernetes/vm/services"
    kubectl $KUBE_APPLY -R -f $EVEREST_SERVICES_DIR $EVEREST_NAMESPACE_N
    #
    # EVEREST Mongo
    #
    EVEREST_MONGO_DIR="./everest/deployment/kubernetes/vm/mongo"
    kubectl $KUBE_APPLY -R -f $EVEREST_MONGO_DIR $EVEREST_NAMESPACE_N
    #
    # EVEREST MONITORING
    #
    EVEREST_MONITORING_DIR="./everest/deployment/kubernetes/vm/monitoring"
    (cd $EVEREST_MONITORING_DIR; kubectl $KUBE_APPLY -R -f grafana/ $EVEREST_NAMESPACE_N)
    
    EVEREST UI
    
    echo "Installing Everest Collector and Web UI..."

    EVEREST_UI_DIR="./everest/deployment/kubernetes/vm/ui"
    COLLECTOR_UI_TMPL="$EVEREST_UI_DIR/collector-uideploy.yaml.TMPL"
    COLLECTOR_UI_YML="/tmp/collector-uideploy.yaml"
    IGRAFANA_SVC_NAME="grafana"
    IKIALI_SVC_NAME="kiali"
    ITRACING_SVC_NAME="tracing"
    PROM_SVC_NAME="prometheus"
    SUFFIX_SVC="outside"
    SVCS="$IKIALI_SVC_NAME $ITRACING_SVC_NAME $IGRAFANA_SVC_NAME $PROM_SVC_NAME"
    if [ "$CLOUD_TYPE" = "vm" ]
    then
        if [ "$OP" != "-d" ]
        then
            SVC_TYPE="NodePort"
            for svc in $SVCS
            do
                kubectl expose -n istio-system svc $svc --type=$SVC_TYPE --name="$svc""-""$SUFFIX_SVC"    
            done
            # PORT_TCP=`kc describe svc istio-grafana-outside -n istio-system |grep $SVC_TYPE | grep -v Type`
            # IGRAFANA_PORT=`echo $PORT_TCP | awk {'print $3'} | cut -d '/' -f 1`
            IGRAFANA_PORT=`kubectl -n istio-system get -o jsonpath="{.spec.ports[0].nodePort}" services "$IGRAFANA_SVC_NAME""-""$SUFFIX_SVC"`
            IGRAFANA_HOST="master"
            IKIALI_PORT=`kubectl -n istio-system get -o jsonpath="{.spec.ports[0].nodePort}" services "$IKIALI_SVC_NAME""-""$SUFFIX_SVC"`
            IKIALI_HOST="master"
            ITRACING_PORT=`kubectl -n istio-system get -o jsonpath="{.spec.ports[0].nodePort}" services "$ITRACING_SVC_NAME""-""$SUFFIX_SVC"`
            ITRACING_HOST="master"
        else
            for svc in $SVCS
            do
                kubectl $KUBE_APPLY -n istio-system svc "$svc""-""$SUFFIX_SVC"    
            done
        fi
    else
        if [ "$OP" != "-d" ]
        then
            # TODO TODO TODO
            SVC_TYPE="LoadBalancer"
            # kubectl expose -n istio-system svc kiali --type=$SVC_TYPE --name=$IKIALI_SVC_NAME  
            # IKIALI_PORT=`kubectl -n istio-system get -o jsonpath="{.spec.ports[0].nodePort}" services $IKIALI_SVC_NAME`
            kubectl expose -n istio-system svc tracing --type=$SVC_TYPE --name=$ITRACING_SVC_NAME  
            kubectl expose -n istio-system svc grafana --type=$SVC_TYPE --name=$IGRAFANA_SVC_NAME 
            kubectl expose -n istio-system svc prometheus --type=$SVC_TYPE --name=$PROM_SVC_NAME   
            ITRACING_HOST=`kubectl -n istio-system get -o jsonpath="{.status.loadBalancer.ingress[0].ip}" services $IGRAFANA_SVC_NAME`
            IGRAFANA_PORT=`kubectl -n istio-system get -o jsonpath="{.spec.ports[0].port}" services $IGRAFANA_SVC_NAME`
            ITRACING_HOST=`kubectl -n istio-system get -o jsonpath="{.status.loadBalancer.ingress[0].ip}" services $ITRACING_SVC_NAME`
            ITRACING_PORT=`kubectl -n istio-system get -o jsonpath="{.spec.ports[0].port}" services $ITRACING_SVC_NAME`
        else
            echo "TODO TODO TODO"
        fi
    fi
    if [ "$OP" != "-d" ]
    then
        echo "Setting IGRAFANA to $IGRAFANA_HOST:$IGRAFANA_PORT on $COLLECTOR_UI_TMPL"
        echo "Setting IKIALI to $IKIALI_HOST:$IKIALI_PORT on $COLLECTOR_UI_TMPL"
        echo "Setting ITRACING to $ITRACING_HOST:$ITRACING_PORT on $COLLECTOR_UI_TMPL"
        sed "s/___IGRAFANA_HOST___:___IGRAFANA_PORT___/$IGRAFANA_HOST:$IGRAFANA_PORT/g;s/___IKIALI_HOST___:___IKIALI_PORT___/$IKIALI_HOST:$IKIALI_PORT/g;s/___ITRACING_HOST___:___ITRACING_PORT___/$ITRACING_HOST:$ITRACING_PORT/g" $COLLECTOR_UI_TMPL > $COLLECTOR_UI_YML
    fi
    kubectl $KUBE_APPLY -f $COLLECTOR_UI_YML $EVEREST_NAMESPACE_N


    echo "Installing Everest Analytics: Flink..."
    FLINK_DIR="./everest/deployment/kubernetes/vm/analytics/flink/session-cluster"
    kubectl $KUBE_APPLY -R -f $FLINK_DIR $EVEREST_NAMESPACE_N
    FLINK_EVEREST_DIR="./everest/deployment/kubernetes/vm/analytics/flink-everest"
    kubectl $KUBE_CREATE $FLINK_EVEREST_DIR/flink-everest.yaml $EVEREST_NAMESPACE_N
    kubectl $KUBE_CREATE $FLINK_EVEREST_DIR/flink-everest-sinks.yaml $EVEREST_NAMESPACE_N


    echo "DONE Installing All Everest Apps and Services ..."
fi

#popd
cd $ORG_DIR
