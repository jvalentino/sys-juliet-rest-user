#!/bin/bash

NAME=sys-golf-rest
VERSION=latest
HELM_NAME=backend

helm delete $HELM_NAME || true
minikube image rm $NAME:$VERSION
rm -rf ~/.minikube/cache/images/arm64/$NAME_$VERSION || true
docker build --no-cache . -t $NAME
minikube image load $NAME:$VERSION
# minikube cache reload
