#!/usr/bin/env python3

import argparse
import sys
import time
import logging
import json
import threading
import signal


from kafka import KafkaProducer
from kafka import KafkaConsumer


class CommandProcessor:
    def __init__(self, config):
        self.logger = logging.getLogger(self.__class__.__name__)
        self.config = config

        self.kill_switch = threading.Event()

        signal.signal(signal.SIGINT, self.shutdown)
        signal.signal(signal.SIGTERM, self.shutdown)
    
    def shutdown(self, *args):
        self.kill_switch.set()
        self.logger.info(f"shutdown called")

    def is_running(self):
        return not self.kill_switch.is_set()

    def process_commands(self):
        self.consumer_thread = threading.Thread(target=self._consume_messages, daemon=True)
        self.consumer_thread.start()

    def _consume_messages(self):
        try:
            kcons = KafkaConsumer(
                self.config.topic_command,
                bootstrap_servers=self.config.kafka_broker,
                group_id=self.config.group_id)
            self.logger.info("consumer running")
            for msg in kcons:
                #logging.info(f"new record: {msg}")

                mesh_command = json.loads(msg.value)

                self._handle_mesh_command(mesh_command)

                if not self.is_running():
                    self.logger.info("consumer ends running")
                    break

        except Exception as ex:
            self.logger.error(f"oops: {ex}")
        finally:
            kcons.close()
            self.logger.info("consumer closed")

    def _handle_mesh_command(self, mesh_command):
        self.logger.info(f"handling command: {mesh_command}")



def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("-k", "--kafka-broker", help="Kafka Broker (bootstrap.servers).", required=True)
    parser.add_argument("--topic-command", default="mesh-command")
    parser.add_argument("--group-id", default="mesh-command-pyprocessor")
    parser.add_argument("-v", "--verbose", help="more verbose.", action="store_true")

    args = parser.parse_args()

    if args.verbose:
        logging.basicConfig(level=logging.INFO)
    else:
        logging.getLogger("CommandProcessor").setLevel(level=logging.INFO)
        logging.basicConfig(level=logging.WARN)

    try:
        processor = CommandProcessor(args)
        processor.process_commands()
        
        try:
            while processor.is_running():
                time.sleep(1)
        except KeyboardInterrupt:
            logging.info("Exiting due to keyboard interrupt")
            processor.shutdown()

    except Exception as ex:
        logging.error(f"Fatal Error: {ex}")
        sys.exit(1)

if __name__ == "__main__":
    main()
