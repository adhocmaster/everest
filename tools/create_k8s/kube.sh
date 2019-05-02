#!/bin/bash
#
# File          :  kube.sh
# Description   :  A helper script to bootstrapp all kubernetes services + all stuffs you like on bare metals or existing vm
#
# Requires      : - Ansible, passwordless ssh into the hosts specified in the file hosts
#
# Change me here into your environment

OP="up"
KUBE=""
FORCE=""

check_dirs() {
    echo "Nothing to check, OK"
}

un_or_install_kube() {
    if [ "a$KUBE" = "a" ]
    then
	ansible-playbook -i ./kube-cluster/hosts ./kube-cluster/initial.yml
	ansible-playbook -i ./kube-cluster/hosts ./kube-cluster/kube-dependencies.yml
	ansible-playbook -i ./kube-cluster/hosts ./kube-cluster/master.yml
	ansible-playbook -i ./kube-cluster/hosts ./kube-cluster/workers.yml
    else
	echo "NOT YET IMPLEMENTED"
    fi
}

install_it() {
    echo "---> ansible $OP"

    if [ "$OP" = "up" ]
    then
	KUBE_DIR=$HOME
        un_or_install_kube
        export KUBECONFIG=$KUBE_DIR/kubeconfig_bm.yml
        ssh ubuntu@master_ip 'sudo cat /etc/kubernetes/admin.conf' > $KUBECONFIG
        echo "export KUBECONFIG=$KUBE_DIR/kubeconfig.yml" > $KUBE_DIR/kubeconfig_bm.sh 
	echo "alias kc='kubectl'" >> $KUBE_DIR/kubeconfig_bm.sh
	chmod ugo+x $KUBE_DIR/kubeconfig_bm.sh
	source $KUBE_DIR/kubeconfig_bm.sh
    else
        un_or_install_kube
    fi

}

for arg in "$@"
do
    case "$1" in
	-h)
	    echo "kube.sh [-h] [-i] [-d] [-f]"
	    echo "deploy or undeploy kubernetes on vm using ansible"
	    echo "-d undeploy vm based kubernetes, by default it is going to deploy"
	    echo "-f delete force (apply only for '-d')"
	    exit
	    ;;
	-d)
	    OP="destroy -f"
	    shift
	    ;;
	-f)
	    FORCE="yes"
	    shift
	    ;;
    esac
done

check_dirs
echo
echo "Start installing Kubernetes on Machines"
install_it
