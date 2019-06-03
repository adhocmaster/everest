#!/bin/sh
#
# Script to build image for eliot demo driver
# Requires:
#      - Docker
#      - Docker Hub account and replace my id below with yours
#      - Access to public internet
#
#
IMAGE_NAME="everest-flink-analytics"
DOCKER_ID="iharijono"
PROJECT="flink-everest"
FLINK_EVEREST_DIR=.
PIPELINE_JAR_DIR=$FLINK_EVEREST_DIR/target
PIPELINE_JAR=$PIPELINE_JAR_DIR/$PROJECT*.jar
PIPELINE_DIR=pipeline

if [ ! -d $PIPELINE_JAR_DIR ]
then
    echo "***** Error ***** : no directory for the job jar files at $PIPELINE_JAR_DIR"
    echo "Suggestion : run 'mvn clean install' on everest flink analytics first to build"
fi
#mvn clean install
cp $PIPELINE_JAR $PIPELINE_DIR

docker build --no-cache -t $IMAGE_NAME .
docker tag $IMAGE_NAME $DOCKER_ID/$IMAGE_NAME:latest
#
# if you are failing here, make sure to login into docker hub
# % docker login
#
docker push iharijono/$IMAGE_NAME:latest
