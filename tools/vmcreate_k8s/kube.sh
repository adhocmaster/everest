#!/bin/bash
#
# File          :  kube.sh
# Description   :  A helper script to bootstrapp all kubernetes services + all stuffs you like on vm
#
# Requires      : - Vagrant and Virtualbox
#
# Change me here into your environment

OP="up"
SCRIPT_DIRS="ubuntu"
KUBE=""
FORCE=""
EVEREST_ONLY=""
KUBE_ONLY=""

check_dirs() {
    if [ "a$KUBE" = "a" ]
    then
        for stuff_dir in $SCRIPT_DIRS
        do
            if [ ! -d "$stuff_dir" ]
            then
                echo "Directory $stuff_dir does NOT exists, are you using complete the repo? Please clone the repo and restart again."
                exit 1
            fi
        done
    fi
}

un_or_install_kube() {
    vagrant $OP
}

create_storage() {
    # $VAGRANT ssh master -c 'mkdir -p /tmp/data-zoo-0; mkdir -p /tmp/data-kafka-0'
    # $VAGRANT ssh node-1 -c 'mkdir -p /tmp/data-zoo-1; mkdir -p /tmp/data-kafka-1'
    # $VAGRANT ssh node-2 -c 'mkdir -p /tmp/data-zoo-2; mkdir -p /tmp/data-kafka-2'
    echo "NOOP"
}

delete_storage() {
    # $VAGRANT ssh master -c 'sudo rm -rf /tmp/data*'
    # $VAGRANT ssh node-1 -c 'sudo rm -rf /tmp/data*'
    # $VAGRANT ssh node-2 -c 'sudo rm -rf /tmp/data*'
    echo "NOOP"
}

INSTALL_EVEREST_SCRIPT=./addon/everest/install-everest.sh 
install_it() {
    echo "---> vagrant $OP"

    if [ "$OP" = "up" ]
    then
        if [ "$EVEREST_ONLY" = "yes" ]
        then
            $INSTALL_EVEREST_SCRIPT
            return
        fi
        KUBE_DIR=$HOME
        un_or_install_kube
        export KUBECONFIG=$KUBE_DIR/kubeconfig.yml
        vagrant ssh master -c 'sudo cat /etc/kubernetes/admin.conf' > $KUBECONFIG
        echo "export KUBECONFIG=$KUBE_DIR/kubeconfig.yml" > $KUBE_DIR/kubeconfig.sh 
        echo "alias kc='kubectl'" >> $KUBE_DIR/kubeconfig.sh
        chmod ugo+x $KUBE_DIR/kubeconfig.sh
        source $KUBE_DIR/kubeconfig.sh
        create_storage
        if [ "$KUBE_ONLY" = "" ]
        then
            echo "installing all everest ---> -$INSTALL_EVEREST_SCRIPT-"
            $INSTALL_EVEREST_SCRIPT
        fi
    else
        delete_storage
        if [ "$EVEREST_ONLY" = "yes" ]
        then
            $INSTALL_EVEREST_SCRIPT  -d
            return
        fi
        un_or_install_kube
    fi

}

for arg in "$@"
do
    case "$1" in
	-h)
	    echo "kube.sh [-h] [-i] [-d] [-k] [-f]"
	    echo "deploy or undeploy kubernetes on vm using vagrant"
        echo "-i deploy or undeploy everest only"
        echo "-k install k8s cluster only"
	    echo "-d undeploy vm based kubernetes, by default it is going to deploy"
	    echo "-f delete force (apply only for '-d')"
	    exit
	    ;;
	-d)
	    OP="destroy -f"
	    shift
	    ;;
	-k)
	    KUBE_ONLY="yes"
	    shift
	    ;;
	-i)
	    EVEREST_ONLY="yes"
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
echo "Start installing Kubernetes on VM"
install_it
