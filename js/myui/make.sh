#!/bin/bash

#set -e

args=("$@")

function build_and_run() {
    npm run build
    docker build -t s7i/fido-web .
    docker run -d -p 8443:8443 -p 5005:5005 --network dev-network --name fido --rm s7i/fido-web
}

function usage() {
    (lolcat || cat) << EOF 2> /dev/null
Usage: spin - run local docker image

EOF
}

case $1 in
  spin)
    build_and_run "${args[@]:1}"
    ;;
  
  *)
  usage
esac
