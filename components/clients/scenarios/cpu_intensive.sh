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

run_cpu() {
    URLS="http://${GATEWAY_URL}/fibo?n= \
        http://${GATEWAY_URL}/fibo_remote?n="

    echo
    echo "Testing Scenario: CPU Usages"

    for i in `seq 40 45`
    echo "Run Computation Nr. $i"
    do
        for _URL in $URLS
        do
            URL="$_URL""$i"
            res=`curl -sL -w "%{http_code}\\n" $URL -o /dev/null`
            check $URL $res
        done
    done
    echo
}
run_cpu