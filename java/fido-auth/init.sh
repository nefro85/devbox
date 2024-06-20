#!/bin/bash


export IP="192.168.0.19"
export CERTSTORE_SECRET="ch@nge1t"
#export PATH=${PATH}:$(cygpath 'C:\develop\tools\apache-maven-3.8.6\bin')
export CERT_PATH=$(pwd)'/labstore.jks'
export USE_SSL=true
export SHOW_ACTIVITY=true


function initCert() {
  keytool \
    -genkeypair \
    -alias rsakey \
    -keyalg rsa \
    -storepass ${CERTSTORE_SECRET} \
    -keystore certstore.jks \
    -storetype JKS \
    -validity 9999 \
    -dname "CN=${IP}.nip.io,O=mario_do_it"

  keytool \
    -importkeystore \
    -srckeystore certstore.jks \
    -destkeystore certstore.jks \
    -deststoretype pkcs12
}

function showCert() {
    keytool \
      -list \
      -rfc \
      -alias rsakey \
      -storepass ${CERTSTORE_SECRET} \
      -keystore certstore.jks \
      -storetype pkcs12

    keytool \
      -export \
      -alias rsakey \
      -storepass ${CERTSTORE_SECRET} \
      -keystore certstore.jks \
      -storetype pkcs12 | openssl x509 -inform der -pubkey -noout
}

function check_jwt() {
  jwt -key key.pub -alg RS256 -verify token.jwt
}

function run() {
  #mvn exec:java
  java -jar target/fido-auth-1.0.0-SNAPSHOT.jar
}



function main () {

    case $1 in
        run)
            run $2
            ;;
        init)
            initCert
            ;;
        cert)
            showCert
            ;;
        jwt)
            check_jwt
            ;;
        *)
        badOpt $@
    esac

}

function badOpt() {
    echo bad options: $@
}



main $@
