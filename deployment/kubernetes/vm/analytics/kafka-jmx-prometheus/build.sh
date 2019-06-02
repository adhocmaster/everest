#!/bin/sh
#
# Script to build image for eliot demo driver
# Requires:
#      - Docker
#      - Docker Hub account and replace my id below with yours
#      - Access to public internet
#
#
IMAGE_NAME="eliot-kafka"
DOCKER_ID="iharijono"

docker build -t $IMAGE_NAME .
docker tag $IMAGE_NAME $DOCKER_ID/$IMAGE_NAME:latest
#
# if you are failing here, make sure to login into docker hub
# % docker login
#
docker push iharijono/$IMAGE_NAME:latest