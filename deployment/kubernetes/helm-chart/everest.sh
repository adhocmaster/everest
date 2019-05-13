#!/bin/bash
#
# File          :  everest.sh
# Description   :  A helper script to bootstrapp all kubernetes services + all stuffs for Everest project with helm
#
# Requires      : - helm
#                 - kubernetes and kubectl
#

usage() {
    echo "$PROG [-h] command [--with-ns namespace] [-d] [-p platform]"
    echo "-h print the usage"
    echo "command can be:"
    echo -e "\tcreate create/install helm installation (default)"
    echo -e "\tdelete delete/uninstall installed helm installation"
    echo -e "\tget get/list helm installation"
    echo "--with-ns install everest in 'namespace' namespace, by default it is set to 'everest'"
    echo "-d run in dry-run mode, no real execution"
    echo "-p (kubernetes) engine/platform, default is 'vm', currently following platform are supported:"
    echo -e "\tvm:\tvirtualbox vm"
    echo -e "\tgke:\tGoogle kubernetes engine"
    #echo -e "\taws:\tAmazon AWS (TBD)"
}

OP="install"
OP0="list"
ENGINE="vm"
PROG="$0"
DRY_RUN=""
HELM_NAME="everest"
HELM_DIR="everest"
WITH_NS="everest"
HELM="helm"

exec_it() {
    HELM_ARGS="--namespace=$WITH_NS"
    if [ "$DRY_RUN" != "" ]
    then
        HELM_ARGS="$HELM_ARGS --debug --dry-run"
    fi
    OP="install"

    echo "$HELM $OP --name=$HELM_NAME $HELM_ARGS $HELM_DIR"
    $HELM $OP $HELM_ARGS $HELM_NAME

    helm del --purge $HELM_NAME


    helm ls $HELM_NAME
    kubectl get all -n $WITH_NS
}


for arg in "$@"
do
    case "$1" in
        -h)
            usage
            exit 0
            ;;
        -d)
            DRY_RUN="--debug --dry-run"
            shift
            ;;
        -p)
            shift
            case $1 in
                vm|gke) ENGINE=$1;;
                *) echo "WARNING: $1 is an unknown engine, it is ignored!";;
            esac
            shift
            ;;
        --with-ns)
            shift
            WITH_NS=$1
            shift
            ;;
        create|install)
            OP="install"
            shift
            ;;
        delete|uninstall)
            OP="delete --purge"
            shift
            ;;
        get|list)
            OP="list --all"
            shift
            ;;
    esac
done

exec_it