#!/bin/bash

CLOUD_TYPE=$1

echo "---> Calling install-master-post-kube.sh -$CLOUD_TYPE-"
if [ "$CLOUD_TYPE" = "" ]
then
    CLOUD_TYPE="vm"
fi


echo "---> Installing Helm and Tille"
curl https://raw.githubusercontent.com/helm/helm/master/scripts/get > get_helm.sh
chmod 700 get_helm.sh
./get_helm.sh

echo "---> Installing Ksonnet"
export KS_VER=0.13.1
export KS_PKG=ks_${KS_VER}_linux_amd64
wget -O /tmp/${KS_PKG}.tar.gz https://github.com/ksonnet/ksonnet/releases/download/v${KS_VER}/${KS_PKG}.tar.gz --no-check-certificate
tar xvf /tmp/$KS_PKG.tar.gz -C /usr/local/bin --strip 1 $KS_PKG/ks

echo "---> Installing MASTER post kube"
echo "kubectl taint nodes --all node-role.kubernetes.io/master-"
kubectl taint nodes --all node-role.kubernetes.io/master-

# Prepare for outputting generated files that are shared later on for others
GENERATED_DIR=/vagrant/generated
echo "-----> deploy kubernetes dashboard"
kubectl -n kube-system create secret tls kdashboard-ui-tls-cert --key=/vagrant/addon/dashboard/tls.key --cert=/vagrant/addon/dashboard/tls.crt
kubectl create -f https://raw.githubusercontent.com/kubernetes/dashboard/master/aio/deploy/recommended/kubernetes-dashboard.yaml
echo "-----> deploy admin role token"	
kubectl apply -f /vagrant/yaml/admin-role.yaml
admin_secret=`kubectl -n kube-system get secret|grep admin-token|cut -d " " -f1`
echo "-----> the dashboard admin secret is:"
echo "$admin_secret"
admin_token=`kubectl -n kube-system describe secret $admin_secret|grep "token:"|tr -s " "|cut -d " " -f2`
echo "-----> the dashboard admin role token is:"
echo "$admin_token"
echo "$admin_token" > $GENERATED_DIR/dashboard_token
echo "-----> login to dashboard with the above token"

uname -r > $GENERATED_DIR/vagrantvm_uname.txt

echo https://$IPADDR:`kubectl -n kube-system get svc kubernetes-dashboard -o=jsonpath='{.spec.ports[0].port}'`


#echo "Installing Metric Servers"
#kubectl create -f /vagrant/mydev/metrics-server/deploy/1.8+/

echo "Installing Tiller"
kubectl create clusterrolebinding add-on-cluster-admin --clusterrole=cluster-admin --serviceaccount=kube-system:default
helm init

echo "Installing Istio"
curl -L https://git.io/getLatestIstio | sh -
ISTIO_VER=`ls -td -- */ | head -n 1 | cut -d'/' -f1`
echo "Installed Istio Version: $ISTIO_VER"
IPATH=`pwd`/$ISTIO_VER/bin
export PATH="$PATH:$IPATH"
(cd $ISTIO_VER; for i in install/kubernetes/helm/istio-init/files/crd*yaml; do kubectl apply -f $i; done)
sleep 30

echo "Setting up permissive demo ISTIO"

SVC_TYPE="NodePort"
if [ "$CLOUD_TYPE" = "vm" ]
then
    sed -i 's/LoadBalancer/NodePort/g' $ISTIO_VER/install/kubernetes/istio-demo.yaml
else
    SVC_TYPE="LoadBalancer"
fi

(cd $ISTIO_VER; kubectl apply -f install/kubernetes/istio-demo.yaml)
kubectl expose -n istio-system svc grafana --type=$SVC_TYPE --name=istio-grafana-outside    

# echo "Install Bookinfo Sample Apps"
# BOOKINFO_APP_DIR="./everest/deployment/kubernetes/vm/app"
# BOOKINFO_APP_NAMESPACE="bookinfo"
# BOOKINFO_APP_NAMESPACE_N="-n $BOOKINFO_APP_NAMESPACE"
# if [ "$BOOKINFO_APP_NAMESPACE" != "default" ]
# then
#     kubectl create namespace $BOOKINFO_APP_NAMESPACE
# fi
# kubectl label namespace $BOOKINFO_APP_NAMESPACE istio-injection=enabled

# (cd $ISTIO_VER; kubectl apply -f samples/bookinfo/platform/kube/bookinfo.yaml $BOOKINFO_APP_NAMESPACE_N)
# (cd $ISTIO_VER; kubectl apply -f samples/bookinfo/networking/destination-rule-all.yaml $BOOKINFO_APP_NAMESPACE_N)

# (cd $ISTIO_VER; kubectl apply -f samples/bookinfo/networking/bookinfo-gateway.yaml $BOOKINFO_APP_NAMESPACE_N)

#
# WARNING:
# who serves outside: 
#       istio-system jaeger-query
#       kubectl expose -n istio-system svc jaeger-query --type=$SVC_TYPE --name=istio-query-outside 
#       istio-system prometheus
#       kubectl expose -n istio-system svc prometheus --type=$SVC_TYPE --name=istio-prometheus-outside 
#
# GKE:
# export INGRESS_HOST=$(kubectl -n istio-system get service istio-ingressgateway -o jsonpath='{.status.loadBalancer.ingress[0].ip}')
# export INGRESS_PORT=$(kubectl -n istio-system get service istio-ingressgateway -o jsonpath='{.spec.ports[?(@.name=="http2")].port}')
# export SECURE_INGRESS_PORT=$(kubectl -n istio-system get service istio-ingressgateway -o jsonpath='{.spec.ports[?(@.name=="https")].port}')
# with hostname:
# export INGRESS_HOST=$(kubectl -n istio-system get service istio-ingressgateway -o jsonpath='{.status.loadBalancer.ingress[0].hostname}')
#
# VM:
#export INGRESS_PORT=$(kubectl -n istio-system get service istio-ingressgateway -o jsonpath='{.spec.ports[?(@.name=="http2")].nodePort}')
#export SECURE_INGRESS_PORT=$(kubectl -n istio-system get service istio-ingressgateway -o jsonpath='{.spec.ports[?(@.name=="https")].nodePort}')
#export INGRESS_HOST=master
#
# export GATEWAY_URL=$INGRESS_HOST:$INGRESS_PORT
#
#
#

#MYDEV=/vagrant/mydev
### 
### 

echo "Retrievee Everest software by git"
sudo apt-get install git
git clone https://github.com/iharijono/everest.git

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
kubectl apply -R -f $KAFKA_DIR

