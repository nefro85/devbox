# Kafka Setup Notes

```bash

#!/bin/bash

#set -x

CONF_FILE=/opt/kafka/kraft-srv.properties
KAFKA_HOME=/opt/kafka/kafka_2.13-3.4.0
CLUSTER_UID="X________mar10_______X"

get_config_option() {
  local option=$1  
  local escaped_option=$(echo ${option} | sed -e "s/\./\\\./g")

  grep -vxE '[[:blank:]]*([#;].*)?' "${CONF_FILE}" \
    | grep -E "^${escaped_option}=.*" \
    | grep -oP "=.{0}\K.*"
}

LOG_DIR=$(get_config_option log.dirs)

echo "Logdir: ${LOG_DIR}"

if [[ ! -e ${LOG_DIR} ]]; then
    ${KAFKA_HOME}/bin/kafka-storage.sh format -t ${CLUSTER_UID} -c ${CONF_FILE}
fi

start_kraft_kafka() {
    echo "Starting Kafka with configuration: ${CONF_FILE}"

    ${KAFKA_HOME}/bin/kafka-server-start.sh ${CONF_FILE}
}

start_kraft_kafka


```
