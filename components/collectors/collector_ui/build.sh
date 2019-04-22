#!/bin/sh
#
# Script to build image for clover collector UI
# Requires:
#      - Docker
#      - Docker Hub account (DOCKER_ID) and replace my id below with yours
#      - Access to public internet
#
#
IMAGE_NAME="collector-ui"
DOCKER_ID="iharijono"
PROJECT="collector-ui"
COLLECTOR_DIR=.
COLLECTOR_UI_DIR=$COLLECTOR_DIR/ui

if [ ! -d $COLLECTOR_UI_DIR ]
then
    echo "***** Error ***** : no directory for the UI files at $COLLECTOR_UI_DIR"
    echo "Suggestion : check out the repo"
    exit 1
fi

(cd $COLLECTOR_UI_DIR; npm run build)
docker build -t $IMAGE_NAME .
docker tag $IMAGE_NAME $DOCKER_ID/$IMAGE_NAME:latest
#
# if you are failing here, make sure to login into docker hub
# % docker login
#
docker push $DOCKER_ID/$IMAGE_NAME:latest
