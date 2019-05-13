#!/bin/bash
#
# File          :  kube_everest.sh
# Description   :  A helper script to bootstrapp all kubernetes services + all stuffs for Everest project with helm
#
# Requires      : - kubectl
#                 - kubernetes
#                 - optionally helm
#

usage() {
    echo "$PROG [-h] command [--with-ns namespace] [--with-helm] [--with-kubectl] [-d] [-p platform] [-f]"
    echo "-h print the usage"
    echo "command can be:"
    echo -e "\tcreate create/install everest installation (default)"
    echo -e "\tdelete delete/uninstall installed everest installation"
    echo -e "\tget get/list everest installation"
    echo "-d run in dry-run mode, no real execution"
    echo "--with-ns install everest in 'namespace' namespace, by default it is set to 'everest'"
    echo "--with-helm run with helm. WARNING: you must have installed helm and tiller"
    echo "--with-kubectl run with plain kubectl command. This is by default"
    echo "-p (kubernetes) engine/platform, default is 'vm', currently following platform are supported:"
    echo -e "\tvm:\tvirtualbox vm (default)"
    echo -e "\tgke:\tGoogle kubernetes engine"
    echo -e "\taws:\tAmazon AWS (TBD)"
    echo "-f quiet, don't ask for confirmation"
}

OP="install"
ENGINE="vm"
PROG="$0"
DRY_RUN=""
KC=kubectl
KC_DIR=""
WITH_HELM=""
WITH_NS="everest"
HELM_CHART_DIR=helm-chart
HELM_SCRIPT=./everest.sh
FORCE=""

exec_it() {
    if [ "$WITH_HELM" != "" ]
    then
        HELM_ARGS="$OP -p $ENGINE"
        if [ "$DRY_RUN" != "" ]
        then
            HELM_ARGS="$HELM_ARGS -d"
        fi
        if [ "$WITH_NS" != "" ]
        then
            HELM_ARGS="$HELM_ARGS --with-ns $WITH_NS"
        fi
        echo "(cd $HELM_CHART_DIR; $HELM_SCRIPT $HELM_ARGS)"
        #(cd $HELM_CHART_DIR; $HELM_SCRIPT $HELM_ARGS)
     else
        KC_ARGS=""
        if [ "$DRY_RUN" != "" ]
        then
            KC_ARGS="--debug --dry-run"
        fi   

        if [ "$OP" = "install" ] ||  [ "$OP" = "create" ] 
        then
            KC_ARGS="$KC_ARGS -n $WITH_NS"
            OP="apply -f"
            KC_DIR="$ENGINE"
            echo "Creating everest namespace"
            #echo "$KC create namespace $WITH_NS"
            $KC create namespace $WITH_NS
            echo "Install everest service"
            #echo "$KC $OP $KC_DIR/services/ $KC_ARGS"
            $KC $OP $KC_DIR/services/ $KC_ARGS 
            echo "Install everest Monitoring: Grafana"
            #echo "$KC $OP $KC_DIR/monitoring/grafana/ $KC_ARGS "
            $KC $OP $KC_DIR/monitoring/grafana/ $KC_ARGS 
            echo "Install everest MongoDB"
            #echo "$KC $OP $KC_DIR/mongo/ $KC_ARGS "
            $KC $OP $KC_DIR/mongo/ $KC_ARGS
            echo "Install everest Collector + UI"
            #echo "$KC $OP $KC_DIR/ui/ $KC_ARGS"
            $KC $OP $KC_DIR/ui/ $KC_ARGS 
        elif [ "$OP" = "get" ] || [ "$OP" = "list" ] 
        then
            echo "Get All Resources on namespace '$WITH_NS'"
            #echo "$KC get all -n $WITH_NS"
            $KC get all -n $WITH_NS
        else
            if [ "$FORCE" = "" ]
            then
                read -r -p "Are you sure to delete ALL in '$WITH_NS'? [y/N] " response
                case "$response" in
                    [yY][eE][sS]|[yY]) 
                        echo "$KC delete namespace $WITH_NS $KC_ARGS"
                        $KC delete namespace $WITH_NS $KC_ARGS
                    ;;
                    *)
                        echo "Abort: do nothing"
                    ;;
                esac
            else
                echo "$KC delete namespace $WITH_NS $KC_ARGS"
                $KC delete namespace $WITH_NS $KC_ARGS
            fi
        fi
    fi
}


for arg in "$@"
do
    case "$1" in
        -h)
            usage
            exit 0
            ;;
        -d)
            DRY_RUN="yes"
            shift
            ;;
        -f)
            FORCE="yes"
            shift
            ;;
        -p)
            shift
            case $1 in
                vm|gke) ENGINE=$1;;
                *) 
                    echo
                    echo "=====> WARNING: $1 is an unknown engine, it is ignored!!!"
                    echo
                    ;;
            esac
            shift
            ;;
        create|install)
            OP="install"
            shift
            ;;
        --with-helm)
            WITH_HELM="yes"
            shift
            ;;
        --with-ns)
            shift
            WITH_NS=$1
            shift
            ;;
        --with-kubectl)
            WITH_HELM=""
            shift
            ;;
        delete|uninstall)
            OP="delete"
            shift
            ;;
        get|list)
            OP="get"
            shift
            ;;
    esac
done
exec_it