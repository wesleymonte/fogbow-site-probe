#!/bin/bash

PREFIX=fogbow-probe
TAG=latest
IMAGE=wesleymonte/probes:${TAG}
NAME=$1

sudo docker pull ${IMAGE}
sudo docker run -itd --name ${PREFIX}-${NAME} -v ~/config/${NAME}/application.properties:/service/config/application.properties -v  ~/config/${NAME}/probe.conf:/service/config/probe.conf ${IMAGE}