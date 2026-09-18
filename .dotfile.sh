#!/bin/bash

# curl -s http://lab.syg:3002/mario/devbox/raw/branch/main/.dotfile.sh | bash -s

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

add_user_2() {
    local name="$1"
    local id="$2"
    local home="$3"

    sudo groupadd --gid "$id" "$name"
    sudo useradd \
        --create-home \
        --home-dir "$home" \
        --uid "$id" \
        --gid "$id" \
        --shell /bin/bash \
        "$name"

    sudo usermod --add-subuids 100000-165535 "$name"
    sudo usermod --add-subgids 100000-165535 "$name"
    sudo loginctl enable-linger "$name"
}
