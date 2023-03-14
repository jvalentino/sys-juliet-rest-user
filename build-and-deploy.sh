#!/bin/bash
./gradlew clean build
./build-docker.sh
./deploy.sh