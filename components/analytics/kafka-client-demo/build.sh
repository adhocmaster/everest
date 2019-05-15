#!/bin/sh
#
# Script to build image for file server 
# Requires:
#      - Docker
#      - Docker Hub account (DOCKER_ID) and replace my id below with yours
#      - Access to public internet
#
#
IMAGE_NAME="kafka-client-demo"
DOCKER_ID="iharijono"
PROJECT="kafka-client-demo"
FS_DIR=./src

if [ ! -d $FS_DIR ]
then
    echo "***** Error ***** : no directory for the files at $FS_DIR"
    echo "Suggestion : check out the repo"
    exit 1
fi

docker build -t $IMAGE_NAME .
docker tag $IMAGE_NAME $DOCKER_ID/$IMAGE_NAME:latest
#
# if you are failing here, make sure to login into docker hub
# % docker login
#
docker push $DOCKER_ID/$IMAGE_NAME:latest
