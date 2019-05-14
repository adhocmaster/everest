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
# It is a client to run simple test on everest related deployment
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


URLS="http://${GATEWAY_URL}/productpage \
    http://${GATEWAY_URL}/fibo?n=3 \
    http://${GATEWAY_URL}/fibo_remote?n=3 \
    http://${GATEWAY_URL}/file?name=everest.ppt \
    http://${GATEWAY_URL}/video?name=small.mp4"

echo
echo "Testing: FS, Fibo REST API"
for URL in $URLS
do
    res=`curl -sL -w "%{http_code}\\n" $URL -o /dev/null`
    check $URL $res
done
echo

#TBD TBD

# echo "Testing: Direct Guide Grpc API TBD TBD TBD"
# (cd guide-client-docker/guide-client && export EVEREST_GUIDE_CLIENT_PORT=9999 && node route_guide_client.js --db_path ./route_guide_db.json)


# URLS="http://${GATEWAY_URL}/guide?goal=1 \
#     http://${GATEWAY_URL}/guide?goal=2"
# echo
# echo "Testing: Guide Grpc via REST"
# for URL in $URLS
# do
#     res=`curl -sL -w "%{http_code}\\n" $URL -o /dev/null`
#     check $URL $res
# done
# echo


URLS="http://${GATEWAY_URL}/guide_remote?goal=1 \
http://${GATEWAY_URL}/guide_fibo?goal=1 \
http://${GATEWAY_URL}/guide_remote?goal=2"
echo
echo "Testing: Remote Guide Grpc via REST"
for URL in $URLS
do
    res=`curl -sL -w "%{http_code}\\n" $URL -o /dev/null`
    check $URL $res
done
echo


