# systemd

### commands

- list services
  ```
  systemctl --type=service --state=running
  ```

### kafka

```ini
[Unit]
Description=Kafka Service
After=network.target

[Service]
User=nefro
Group=nefro
Type=simple
Environment=JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
ExecStart=/opt/kafka/kafka_2.13-2.8.0/bin/kafka-server-start.sh /opt/kafka/kafka_2.13-2.8.0/config/server.properties

[Install]
WantedBy=multi-user.target

```


### rabbitmq

```bash
systemctl is-enabled rabbitmq-server
sudo systemctl disable rabbitmq-server

```
