#!/usr/bin/env bash

BOX=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
export REMOTE="dwarf.syg:5817/docker/"

cd $BOX/java/fido-auth
./build-docker.sh "$@"

#exit 0

if [[ "x${NO_JS}" != "x1" ]]; then
  echo "running js build"
  cd $BOX/js
  docker compose up
fi

cd $BOX/js/myui
docker build \
  -t "${REMOTE}s7i/fido-web" \
  --build-arg AUTH_IMAGE="${REMOTE}s7i/fido-auth:latest" .

if [[ -n "${REMOTE}" ]]; then
  docker push "${REMOTE}s7i/fido-web"
fi

cd $BOX
