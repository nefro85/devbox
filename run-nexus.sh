#!/bin/bash

#docker volume create --name nexus-data

docker run -d \
-p "${NEXUS_PORT:-5815}:8081" \
-p "5814:5814" \
--name nexus -v nexus-data:/nexus-data sonatype/nexus3

