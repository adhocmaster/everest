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
# WARNING: a copy from clover project, please synch up!!!
#
# on vagrant based vm, the parameter currently is '4.15.0-45-generic'
#
#

#GENERATED_DIR=/vagrant/GENERATED_DIR
GENERATED_DIR=../vmcreate_k8s/generated
GENERATED_UNAME=$GENERATED_DIR/vagrantvm_uname.txt

IMAGE_NAME="clovisor"
DOCKER_ID="iharijono"
PROJECT="clovisor"
FS_DIR=.

if [ ! -d $FS_DIR ]
then
    echo "***** Error ***** : no directory for the files at $FS_DIR"
    echo "Suggestion : check out the repo"
    exit 1
fi

if [ -z "$1" ]
  then
    #kernel_ver=linux-headers-`uname -r`
    kernel_ver=linux-headers-`cat $GENERATED_UNAME`
  else
    kernel_ver=$1
fi

#
# This is copied from CLOVER !!!!
#
cp bin/clovisor .
docker build --build-arg TARGET_KERNEL_VER=$kernel_ver -t $IMAGE_NAME .
docker tag $IMAGE_NAME $DOCKER_ID/$IMAGE_NAME:latest
#
# if you are failing here, make sure to login into docker hub
# % docker login
#
docker push $DOCKER_ID/$IMAGE_NAME:latest
#docker tag clovisor s3wong/clovisor
#docker push s3wong/clovisor
