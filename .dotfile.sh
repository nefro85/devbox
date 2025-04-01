#!/bin/bash

figlet s7i dotfile | lolcat

function get_ssl_cert() {
    local ENDPOINT="$1"
    openssl s_client -showcerts -connect "${ENDPOINT}" < /dev/null \
          | sed -ne '/-BEGIN CERTIFICATE-/,/-END CERTIFICATE-/p'
}


function add_user() {
    local name="$1"
    local id="$2"
    local home="$3"

    sudo groupadd --gid=${id} ${name}
    sudo useradd  --home-dir ${home} --uid=${id} --gid=${name} ${name}
}

