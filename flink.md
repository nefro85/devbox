# Flink notes


## Installation

```bash
set -ex

FLINK_VERSION=1.16.1
FLINK_SCALA_VERSION=2.12


SW_FLINK_GZ="https://www.apache.org/dyn/closer.cgi?action=download&filename=flink/flink-${FLINK_VERSION}/flink-${FLINK_VERSION}-bin-scala_${FLINK_SCALA_VERSION}.tgz"
DEST=/opt/flink/flink_${FLINK_VERSION//./_}

mkdir -p ${DEST}

wget -nv -O ./flink.gz "$SW_FLINK_GZ"
tar -xf ./flink.gz -C ${DEST} --strip-components=1; \
rm ./flink.gz
```

## Local configuration

```yaml
state.backend: rocksdb
state.checkpoints.dir: file:/opt/flink/data/checkpoints
state.savepoints.dir: file:/opt/flink/data/savepoints
state.backend.incremental: true
state.backend.rocksdb.timer-service.factory: rocksdb

rest.flamegraph.enabled: true
env.java.opts.jobmanager: "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
env.java.opts.taskmanager: "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5006"

execution.checkpointing.interval: 3min
execution.checkpointing.externalized-checkpoint-retention: DELETE_ON_CANCELLATION
execution.checkpointing.max-concurrent-checkpoints: 1
execution.checkpointing.min-pause: 0
execution.checkpointing.mode: AT_LEAST_ONCE
execution.checkpointing.timeout: 10min
execution.checkpointing.tolerable-failed-checkpoints: 0
execution.checkpointing.unaligned: false

```
