#!/bin/bash

figlet s7i dotfile | lolcat

function get_ssl_cert() {
    local ENDPOINT="$1"
    openssl s_client -showcerts -connect "${ENDPOINT}" < /dev/null \
          | sed -ne '/-BEGIN CERTIFICATE-/,/-END CERTIFICATE-/p'
}

