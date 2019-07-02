#!/usr/bin/env bash

export INGRESS_PORT=$(kubectl -n istio-system get service istio-ingressgateway -o jsonpath='{.spec.ports[?(@.name=="http2")].nodePort}')
export INGRESS_HOST="master"
export GATEWAY_URL=$INGRESS_HOST:$INGRESS_PORT

declare -A NUMMAP
if [ -z "$1" ]
then
	TOTAL=500
else
	TOTAL=$1
fi
echo "launch $TOTAL requests..."

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

URLS="http://${GATEWAY_URL}/fibo?n=3 \
    http://${GATEWAY_URL}/fibo_remote?n=3 \
    http://${GATEWAY_URL}/file?name=everest.ppt \
    http://${GATEWAY_URL}/video?name=small.mp4"


for (( i = 0; i < $TOTAL; i++ )) 
do
  for URL in $URLS
  do
    RET=$(curl -sL -w "%{http_code}\\n" $URL -o /dev/null)
    if [ -z "$RET" -o "$RET" == "\n" -o "$RET" == "no healthy upstream" ]
    then
        RET="failure"
    fi
    NUMMAP[${RET}]=$((NUMMAP[${RET}]+1))
  done
done

KEYS=(${!NUMMAP[@]})
for (( I=0; $I < ${#NUMMAP[@]}; I+=1 )); do
  KEY=${KEYS[$I]}
  V=${NUMMAP[$KEY]}
  PERCENT=$(bc <<< "scale=3;$V*400/$TOTAL")
  echo "$KEY : ${NUMMAP[$KEY]}(${PERCENT}%)"
done
