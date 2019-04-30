#!/bin/bash

echo "---> Installing kubeconfig"
echo "mkdir -p $HOME/.kube"
mkdir -p $HOME/.kube

if [[ -f "/etc/kubernetes/admin.conf" ]]
then
    cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
    chown $(id -u):$(id -g) $HOME/.kube/config
###    USER=ubuntu
###    USER_HOME=/home/$USER
###    mkdir -p $USER_HOME/.kube
###    cp -i /etc/kubernetes/admin.conf $USER_HOME/.kube/config
###    chown $(id -u $USER):$(id -g $USER) $USER_HOME/.kube/config
    cp /etc/kubernetes/admin.conf /vagrant/
else
    cp -i /vagrant/admin.conf $HOME/.kube/config
fi
#USER=ubuntu
USER=vagrant
USER_HOME=/home/$USER
mkdir -p $USER_HOME/.kube
cp -i /vagrant/admin.conf $USER_HOME/.kube/config
chown $(id -u $USER):$(id -g $USER) $USER_HOME/.kube/config
echo "alias kc='kubectl'" >> $USER_HOME/.bash_aliases
