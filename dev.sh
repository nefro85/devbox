#!/usr/bin/env bash

BOX=$(pwd)
export REMOTE="dwarf.syg:5817/docker/"

cd $BOX/java/fido-auth
./build-docker.sh "$@"

#exit 0

cd $BOX/js
docker compose up

cd $BOX/js/myui
docker build \
  -t "${REMOTE}s7i/fido-web" \
  --build-arg AUTH_IMAGE="${REMOTE}s7i/fido-auth:latest" .
docker push "${REMOTE}s7i/fido-web"

cd $BOX

