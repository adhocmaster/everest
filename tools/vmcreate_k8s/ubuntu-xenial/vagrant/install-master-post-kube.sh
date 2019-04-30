#!/bin/bash

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
#kubectl apply -f https://raw.githubusercontent.com/kubernetes/dashboard/master/src/deploy/recommended/kubernetes-dashboard.yaml
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

echo https://$IPADDR:`kubectl -n kube-system get svc kubernetes-dashboard -o=jsonpath='{.spec.ports[0].port}'`

echo "Installing Tiller"
kubectl create clusterrolebinding add-on-cluster-admin --clusterrole=cluster-admin --serviceaccount=kube-system:default
helm init


#MYDEV=/vagrant/mydev
#SKYLARK_ROOT=/vagrant/mydev/skylark/skylark
#MYELIOT=$MYDEV/skylark/skylark/flink-eliot/tools/deployment/kubernetes
#$MYELIOT/deploy.sh -root $SKYLARK_ROOT

#
# Let's set up all Kubernetes services and all stuffs for Eliot/Flink jobs
#
# Kafka (needs helm above)
### (cd $SKYLARK_ROOT/cp-helm-charts; helm install --name kafka .)
### (cd $SKYLARK_ROOT/eliot-prometheus-charts; helm install --name prometheus .)
### (cd $SKYLARK_ROOT/eliot-helm-charts; helm install --name eliot .)



### 
### #
### # Cassandra
### # WARNING: Replace this with cassandra service from others such as from clover etc. if you already
### # have one running
### # 
### MYDEV_CASSANDRA=$MYDEV/skylark/skylark/cassandra-kubernetes
### kubectl apply -f $MYDEV_CASSANDRA
### 
### # Monitoring
### # InfluxDB
### MYDEV_MONITORING=$MYDEV/skylark/skylark/monitoring-eliot
### kubectl create -f $MYDEV_MONITORING/00-namespace.yaml
### MYDEV_INFLUXDB=$MYDEV_MONITORING/influxdb
### kubectl create -f $MYDEV_INFLUXDB/10influxdb-service.yaml
### kubectl create -f $MYDEV_INFLUXDB/20influxdb-deployment.yaml
### # Grafana
### MYDEV_GRAFANA=$MYDEV_MONITORING/grafana
### kubectl create -f $MYDEV_GRAFANA/10grafana-service.yaml
### kubectl create -f $MYDEV_GRAFANA/20grafana-deployment.yaml
### kubectl create -f $MYDEV_GRAFANA/30grafana-outside-service.yaml
### 
### # Kafka
### MYDEV_KAFKA=$MYDEV/skylark/skylark/kafka-clover
### 
### #kubectl create -f $MYDEV_KAFKA/kafka-service/00-namespace.yml
### #kubectl apply -R -f $MYDEV_KAFKA/kafka-service/rbac-namespace-default
### #kubectl apply -R -f $MYDEV_KAFKA/kafka-service/zookeeper
### #kubectl apply -R -f $MYDEV_KAFKA/kafka-service/kafka
### #kubectl apply -R -f $MYDEV_KAFKA/kafka-service/outside-services
### 
### kubectl apply -R -f $MYDEV_KAFKA/kafka-service
### #kubectl apply -R -f $MYDEV_KAFKA/kafka-service-dashboard
### 
### # Flink
### MYDEV_FLINK=$MYDEV/skylark/skylark/flink-kubernetes/session-cluster
### MYDEV_FLINK_SHARE=$MYDEV/skylark/skylark/flink-kubernetes/session-cluster/shared-storage
### kubectl apply -f $MYDEV_FLINK/0flink-namespace.yaml
### kubectl apply -f $MYDEV_FLINK_SHARE/00pv.yaml
### kubectl apply -f $MYDEV_FLINK_SHARE/01pvc.yaml
### 
### kubectl apply -f $MYDEV_FLINK/00jobmanager-service.yaml
### kubectl apply -f $MYDEV_FLINK/01jobmanager-deployment.yaml
### kubectl apply -f $MYDEV_FLINK/02taskmanager-deployment.yaml
### kubectl apply -f $MYDEV_FLINK/03flink-outside-service.yaml
### 
### #
### # Eliot Tools
### #
### MYDEV_ELIOT_TOOLS=$MYDEV/skylark/skylark/flink-eliot/tools/deployment/kubernetes
### kubectl apply -f $MYDEV_ELIOT_TOOLS/tools/eliot-tools.yaml
### 
### 
