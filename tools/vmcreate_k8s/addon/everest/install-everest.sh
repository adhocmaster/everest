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
sleep 30

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
kubectl apply -f $EVEREST_UI_DIR $EVEREST_NAMESPACE_N

echo "DONE Installing Everest Service ..."

popd