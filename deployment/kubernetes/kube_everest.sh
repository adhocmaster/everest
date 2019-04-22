#!/bin/bash
#
# File          :  kube_everest.sh
# Description   :  A helper script to bootstrapp all kubernetes services + all stuffs for Everest project with helm
#
# Requires      : - kubectl
#                 - kubernetes
#

usage() {
    echo "$PROG [-h] command [-d] [-p platform]"
    echo "-h print the usage"
    echo "command can be:"
    echo -e "\tcreate create/install everest installation (default)"
    echo -e "\tdelete delete/uninstall installed everest installation"
    echo -e "\tget get/list everest installation"
    echo "-d run in dry-run mode, no real execution"
    echo "-p (kubernetes) engine/platform, default is 'vm', currently following platform are supported:"
    echo -e "\tvm:\tvirtualbox vm"
    echo -e "\tgke:\tGoogle kubernetes engine"
    echo -e "\taws:\tAmazon AWS (TBD)"
}

OP="install"
OP0="list"
ENGINE="vm"
PROG="$0"
DRY_RUN=""
CMD=kubectl
exec_it() {
    $(CMD) $OP --name everest $DRY_RUN $HELM_NAME
}

execs_it() {
    $(CMD) $OP --name everest $DRY_RUN $HELM_NAME
    $(CMD) $OP0 --name everest $DRY_RUN $HELM_NAME
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
            ENGINE=$1
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

if [ "$OP" = "list --all"]
then
    execs_it
else
    exec_it
fi