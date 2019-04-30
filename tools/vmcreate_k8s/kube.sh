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
    if [ "a$KUBE" = "a" ]
    then
        vagrant $OP
    fi
}

install_it() {
    echo "---> vagrant $OP"

    if [ "$OP" = "up" ]
    then
        un_or_install_kube
        export KUBECONFIG=$(pwd)/kubeconfig.yml
        vagrant ssh master -c 'sudo cat /etc/kubernetes/admin.conf' > $KUBECONFIG
        echo 'export KUBECONFIG=$(pwd)/kubeconfig.yml' > kubeconfig.sh 
	echo "alias kc='kubectl'" >> kubeconfig.sh
	chmod ugo+x kubeconfig.sh
	source kubeconfig.sh
    else
        un_or_install_kube
    fi

}

for arg in "$@"
do
    case "$1" in
	-h)
	    echo "kube.sh [-h] [-i] [-d] [-f]"
	    echo "deploy or undeploy kubernetes on vm using vagrant"
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
echo "Start installing Kubernetes on VM"
install_it
