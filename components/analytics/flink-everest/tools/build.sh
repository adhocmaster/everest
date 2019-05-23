#!/bin/sh
#
# Script to build image for eliot demo driver
# Requires:
#      - Docker
#      - Docker Hub account and replace my id below with yours
#      - Access to public internet
#
#
IMAGE_NAME="eliot-tools"
DOCKER_ID="iharijono"
PROJECT="flink-eliot"
ELIOT_DIR=../../$PROJECT
PIPELINE_JAR_DIR=$ELIOT_DIR/target
PIPELINE_JAR=$PIPELINE_JAR_DIR/$PROJECT*.jar
PIPELINE_DIR=pipeline

if [ ! -d $ELIOT_DIR ]
then
    echo "***** Error ***** : no directory for the job jar files at $ELIOT_DIR"
    echo "Suggestion : run 'mvn clean install' on eliot flink java project first to build"
fi
cp $PIPELINE_JAR $PIPELINE_DIR

docker build -t $IMAGE_NAME .
docker tag $IMAGE_NAME $DOCKER_ID/$IMAGE_NAME:latest
#
# if you are failing here, make sure to login into docker hub
# % docker login
#
docker push iharijono/$IMAGE_NAME:latest