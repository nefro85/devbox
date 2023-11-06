FROM eclipse-temurin:11


WORKDIR /opt/fido2

COPY misc/hello.html web/index.html
COPY certstore.jks certstore.jks
COPY target/fido-auth-1.0.0-SNAPSHOT.jar fido.jar

ENV APP_NAME="Web Auth Service" \
    APP_FLAGS="dev" \
    AUTH_ATTACHMENT="CROSS_PLATFORM" \
    CERTSTORE_SECRET="ch@nge1t" \
    CERT_PATH="certstore.jks" \
    USE_SSL="true" \
    SHOW_ACTIVITY="true" \
    ORIGIN="https://localhost:8443" \
    MONGODB_URI="mongodb://root:example@mongo:27017/" \
    REPO_TYPE="rocksdb" \
    ROCKSDB_PATH="./authu-rocksdb" \
    JVM_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005" \
    WEB_ROOT="web"

CMD ["sh", "-c", "java ${JVM_OPTS} -jar fido.jar"]

EXPOSE 8443
EXPOSE 5005
