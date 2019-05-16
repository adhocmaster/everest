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
# Requires: 
#     - sudo
#     - kubeconfig
#     - kubectl
#

pushd /tmp

#
# WARNING WARNING WARNING: be careful here
# don't do this if you are INSIDE THE VM
# do this if you are outside the VM
#
if [ "$KUBECONFIG" = "" ]
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
#
# EVEREST ANALYTICS
#
#  WARNING WARNING WARNING: adjust namespace in kafka-service/00rbac-namespace-default/10-node-reader.yml
#
echo "Installing Kafka ..."
KAFKA_NAMESPACE="kafka"
KAFKA_NAMESPACE_N="-n $KAFKA_NAMESPACE"
KAFKA_DIR="./everest/deployment/kubernetes/vm/analytics/kafka-service"

if [ "$KAFKA_APP_NAMESPACE" != "default" ]
then
    kubectl create namespace $KAFKA_NAMESPACE
fi
kubectl apply -R -f $KAFKA_DIR $KAFKA_NAMESPACE_N

echo "DONE Installing Kafka, wait ..."
sleep 120

#
#
# EVEREST APP SAMPLE
#
#
echo "Install Everest Sample Apps"
EVEREST_APP_DIR="./everest/deployment/kubernetes/vm/app"
EVEREST_APP_NAMESPACE="everest-app"
EVEREST_APP_NAMESPACE_N="-n $EVEREST_APP_NAMESPACE"
if [ "$EVEREST_APP_NAMESPACE" != "default" ]
then
    kubectl create namespace $EVEREST_APP_NAMESPACE
fi
kubectl label namespace $EVEREST_APP_NAMESPACE istio-injection=enabled
(cd $EVEREST_APP_DIR; kubectl apply -f file-server/file-server.yaml $EVEREST_APP_NAMESPACE_N)
(cd $EVEREST_APP_DIR; kubectl apply -f file-server/fs-istio-gateway.yaml $EVEREST_APP_NAMESPACE_N)
(cd $EVEREST_APP_DIR; kubectl apply -f file-server/fs-istio-destinationrules.yaml $EVEREST_APP_NAMESPACE_N)
(cd $EVEREST_APP_DIR; kubectl apply -f guide/guide-server.yaml $EVEREST_APP_NAMESPACE_N)
(cd $EVEREST_APP_DIR; kubectl apply -f guide/guide-istio-gateway.yaml $EVEREST_APP_NAMESPACE_N)
(cd $EVEREST_APP_DIR; kubectl apply -f guide/guide-istio-destinationrules.yaml $EVEREST_APP_NAMESPACE_N)

echo "DONE Installing Everest Sample Apps, wait ..."
sleep 10


echo "Install ALL Everest Apps"
EVEREST_NAMESPACE="everest"
EVEREST_NAMESPACE_N="-n $EVEREST_NAMESPACE"
if [ "$EVEREST_APP_NAMESPACE" != "default" ]
then
    kubectl create namespace $EVEREST_NAMESPACE
fi

#
# EVEREST services
#
EVEREST_SERVICES_DIR="./everest/deployment/kubernetes/vm/services"
kubectl apply -f $EVEREST_SERVICES_DIR $EVEREST_NAMESPACE_N
#
# EVEREST Mongo
#
EVEREST_MONGO_DIR="./everest/deployment/kubernetes/vm/mongo"
kubectl apply -f $EVEREST_MONGO_DIR $EVEREST_NAMESPACE_N
#
# EVEREST MONITORING
#
EVEREST_MONITORING_DIR="./everest/deployment/kubernetes/vm/monitoring"
(cd $EVEREST_MONITORING_DIR; kubectl apply -f grafana/ $EVEREST_NAMESPACE_N)
#
# EVEREST UI
#
EVEREST_UI_DIR="./everest/deployment/kubernetes/vm/ui"
COLLECTOR_UI_TMPL="$EVEREST_UI_DIR/collector-uideploy.yaml.TMPL"
COLLECTOR_UI_YML="/tmp/collector-uideploy.yaml"
IGRAFANA_SVC_NAME="istio-grafana-outside"
CLOUD_TYPE="vm"
if [ "$CLOUD_TYPE" = "vm" ]
then
    SVC_TYPE="NodePort"
    kubectl expose -n istio-system svc grafana --type=$SVC_TYPE --name=$IGRAFANA_SVC_NAME    
    # PORT_TCP=`kc describe svc istio-grafana-outside -n istio-system |grep $SVC_TYPE | grep -v Type`
    # IGRAFANA_PORT=`echo $PORT_TCP | awk {'print $3'} | cut -d '/' -f 1`
    IGRAFANA_PORT=`kubectl -n istio-system get -o jsonpath="{.spec.ports[0].nodePort}" services $IGRAFANA_SVC_NAME`
    IGRAFANA_HOST="master"
else
    # TODO TODO TODO
    SVC_TYPE="LoadBalancer"
    kubectl expose -n istio-system svc grafana --type=$SVC_TYPE --name=$IGRAFANA_SVC_NAME    
    IGRAFANA_PORT=3000
    IGRAFANA_HOST="TBD"
fi
echo "Setting IGRAFANA to $IGRAFANA_HOST:$IGRAFANA_PORT on $COLLECTOR_UI_TMPL"
sed "s/___IGRAFANA_HOST___:___IGRAFANA_PORT___/$IGRAFANA_HOST:$IGRAFANA_PORT/g" $COLLECTOR_UI_TMPL > $COLLECTOR_UI_YML
# kubectl apply -f $EVEREST_UI_DIR $EVEREST_NAMESPACE_N
kubectl apply -f $COLLECTOR_UI_YML $EVEREST_NAMESPACE_N

echo "DONE Installing Everest Service ..."

popd