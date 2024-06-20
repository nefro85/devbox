FROM eclipse-temurin:11


WORKDIR /opt/fido2

COPY misc/hello.html web/index.html
COPY labstore.jks sslcertstore.jks
COPY certstore.jks jwtcertstore.jks
COPY target/fido-auth.jar fido.jar

ENV APP_NAME="Web Auth Service" \
    APP_FLAGS="dev" \
    AUTH_ATTACHMENT="CROSS_PLATFORM" \
    CERTSTORE_SECRET="test123" \
    CERT_PATH="sslcertstore.jks" \
    JWT_CERTSTORE_PATH="jwtcertstore.jks" \
    JWT_CERTSTORE_SECRET="ch@nge1t" \
    JWT_CERT_ALIAS="rsakey" \
    JWT_TTL_HOURS="24"\
    USE_SSL="true" \
    SHOW_ACTIVITY="true" \
    ORIGIN="https://lab.mariodoit.pl" \
    MONGODB_URI="mongodb://root:example@mongo:27017/" \
    REPO_TYPE="rocksdb" \
    ROCKSDB_PATH="./authu-rocksdb" \
    JVM_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005" \
    WEB_ROOT="web"

CMD ["sh", "-c", "java ${JVM_OPTS} -jar fido.jar"]

EXPOSE 8443
EXPOSE 5005
