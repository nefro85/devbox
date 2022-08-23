@echo off
@rem run docker image

echo arg kafa broker: %1
echo arg network: %2

docker run ^
-d ^
-p :8080 ^
-e KAFKA_BROKERS=%1 ^
--network=%2 ^
--name redpanda ^
docker.redpanda.com/vectorized/console:master-0a8fce8
