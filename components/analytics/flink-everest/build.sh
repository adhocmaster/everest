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
EVEREST_SINKS_PYTHONS=../kafka-client-demo
PIPELINE_SINKS_DIR=$PIPELINE_DIR/sinks

if [ ! -d $PIPELINE_JAR_DIR ]
then
    echo "***** Error ***** : no directory for the job jar files at $PIPELINE_JAR_DIR"
    echo "Suggestion : run 'mvn clean install' on everest flink analytics first to build"
fi
# NOW populate the image with the generated directories and files from  projects

# Flink Everest artifacts (jar files)
# mvn clean install
cp $PIPELINE_JAR $PIPELINE_DIR

# Sinks everest artifacts (python3)
cp $EVEREST_SINKS_PYTHONS/requirements.txt $PIPELINE_SINKS_DIR
cp $EVEREST_SINKS_PYTHONS/src/* $PIPELINE_SINKS_DIR


docker build --no-cache -t $IMAGE_NAME .
docker tag $IMAGE_NAME $DOCKER_ID/$IMAGE_NAME:latest
#
# if you are failing here, make sure to login into docker hub
# % docker login
#
docker push iharijono/$IMAGE_NAME:latest
