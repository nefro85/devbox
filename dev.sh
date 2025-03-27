#!/usr/bin/env bash

BOX=$(pwd)

cd $BOX/java/fido-auth
./build-docker.sh

cd $BOX/js
docker compose up

cd $BOX/js/myui
docker build -t dwarf.syg:5817/docker/s7i/fido-web .
docker push dwarf.syg:5817/docker/s7i/fido-web

cd $BOX

