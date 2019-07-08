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
# It is a client to run scenario test on everest with high network usage on Istio everest apps
#
#


source ./common.sh
PROG=$0
ITER_NUM=1

# generate 100 MB file under /tmp/file.txt
gen_file 200 1048576
LARGE_F_URL=$DEFAULT_FILE

# curl -v -F myFile=@/usr/bin/ssh http://localhost:9000/uploadfile
# cat mem_intensive.sh | curl -v -F myFile=@- http://localhost:9000/uploadfile
# curl -vs $LARGE_F_URL 2>&1 | curl -v -F myFile=@- http://localhost:9000/uploadfile
run_network() {
    URLS="http://${GATEWAY_URL}/file?name=everest.ppt \
        http://${GATEWAY_URL}/video?name=combat.mov \
        http://${GATEWAY_URL}/video?name=music-box.mp4 \
        http://${GATEWAY_URL}/video?name=small.mp4"

    l_files=`ls`

    # echo
    # echo "Testing Scenario: Network Usages"

    for i in `seq 1 11`
    do
        echo "Download files and video Nr. $i"
        for URL in $URLS
        do
            res=`curl -sL -w "%{http_code}\\n" $URL -o /dev/null`
            check $URL $res
        done
        echo "Uploading large file Nr. $i"
        URL="http://${GATEWAY_URL}/uploadfile"
        res=`curl --silent -w "%{http_code}\\n" -F myFile=@$LARGE_F_URL $URL -o /dev/null`
        check "$URL@$LARGE_F_URL" $res
        for file in $l_files
        do
            #echo "Uploading file $file"
            res=`curl --silent -w "%{http_code}\\n" -F myFile=@$file $URL -o /dev/null`
            check "$URL@$file" $res
        done

    done
    echo
}
for arg in "$@"
do
    if [ "$1" = "" ]
    then
        shift
        continue
    fi
    case "$1" in
	-h)
        usage $PROG "run network task"
	    exit
	    ;;
	-n)
        shift
	    ITER_NUM=$1
	    shift
	    ;;
	*)
	    echo "Parameter error -$1-"
	    shift
        usage
        exit 1
	    ;;
    esac
done

for i in $(seq 0 $ITER_NUM)
do
    run_network
done

# # TODO TODO TODO
# URLS="http://${GATEWAY_URL}/guide_chat?goal="

# echo
# echo "Testing Network Guide Chat: Network Usages"

# for i in `seq 200 210`
# echo "Run Computation Nr. $i"
# do
#     for _URL in $URLS
#     do
#         URL="$_URL""$i"
#         res=`curl -sL -w "%{http_code}\\n" $URL -o /dev/null`
#         check $URL $res
#     done
# done
# echo
