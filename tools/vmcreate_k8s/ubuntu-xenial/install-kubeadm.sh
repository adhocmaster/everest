#!/bin/bash
apt-get update && apt-get install -y apt-transport-https
curl --silent https://packages.cloud.google.com/apt/doc/apt-key.gpg | apt-key add -
cat <<EOF > /etc/apt/sources.list.d/kubernetes.list
deb http://apt.kubernetes.io/ kubernetes-xenial main
EOF
apt-get update && apt-get install -y kubelet kubeadm kubectl

swapoff -a
###echo "Installing Helm and Tille"
###curl https://raw.githubusercontent.com/helm/helm/master/scripts/get > get_helm.sh
###chmod 700 get_helm.sh
###./get_helm.sh
###
