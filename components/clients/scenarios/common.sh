#!/bin/sh
#
#
# Copyright 2017-2019 The Everest Authors
#
# Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
# in compliance with the License. You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software distributed under the License
# is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
# or implied. See the License for the specific language governing permissions and limitations under
# the License.
#
# 
# 
#
# It is a client to run scenario test on everest with high cpu usage on Istio everest apps
#
#

export INGRESS_PORT=$(kubectl -n istio-system get service istio-ingressgateway -o jsonpath='{.spec.ports[?(@.name=="http2")].nodePort}')
export SECURE_INGRESS_PORT=$(kubectl -n istio-system get service istio-ingressgateway -o jsonpath='{.spec.ports[?(@.name=="https")].nodePort}')
export INGRESS_HOST="master"

export GATEWAY_URL=$INGRESS_HOST:$INGRESS_PORT
#export GATEWAY_URL=localhost:9000

usage() {
    cmd=$1
    desc=$2
    echo "$cmd [-h] [-n iter_num] [-f]"
    echo "$desc against everest-app"
    echo "-h print this page"
    echo "-n iter_num run loop with iter_num iterations"
    echo "-f make it more excessive"
}

check() {
    url=$1
    res=$2
    if [ $res -ne 200 ]
    then
        echo "***** Error ***** FAILED: to execute $url return_code: -$res-"
    else
         echo "'$url' --> SUCCESS"   
    fi
}

DEFAULT_COUNT=1024
#MegaByte
#DEFAULT_UNIT=1048576
#KiloByte
DEFAULT_UNIT=1024
DEFAULT_FILE=/tmp/file.txt

gen_file() {
    count=$1
    unit=$2

    if [ "$count" = "" ]
    then
        count=DEFAULT_COUNT
    fi
    if [ "$unit" = "" ]
    then
        unit=DEFAULT_UNIT
    fi

    dd if=/dev/urandom of=$DEFAULT_FILE bs=$unit count=$count

}