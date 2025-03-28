#!/usr/bin/env python3

import argparse
import sys
import time
import logging
import base64

from pubsub import pub
from kafka import KafkaProducer

import meshtastic
import meshtastic.serial_interface
import meshtastic.tcp_interface

class MeshHandler():
    def __init__(self, useKafka:bool = None, bootstrap_servers:str = None, topicMesh:str="meshtastic-package", verbose:bool=False):
        
        self.useKafka = useKafka
        self.topicName = topicMesh
        self.device_id = None
        self.verbose = verbose

        if useKafka:
            self.producer = KafkaProducer(bootstrap_servers=bootstrap_servers)

    # @property
    # def topic(self):
    #     return self.topicName

    # @topic.setter
    def topic(self, name):
        self.topicName = name
    
    def _publishToKafka(self, binary_data:bytes) -> None:
        if self.useKafka:
            key = None 
            if self.device_id:
                key = self.device_id.encode()
            self.producer.send(self.topicName, key=key, value=binary_data)

    def onReceive(self, packet, interface, topic=pub.AUTO_TOPIC):  # pylint: disable=unused-argument
        bin: bytes = packet['raw'].SerializeToString()
        
        if self.verbose:
            keys_list = list(packet.keys())
            for key in keys_list:
                if key == 'raw':
                    logging.info(f"[BASE64]{base64.b64encode(bin).decode('utf-8')}")
                logging.info(f"{key} : {type(packet[key])} => {packet[key]}")

        self._publishToKafka(bin)


    def onConnection(self, interface, topic=pub.AUTO_TOPIC):  # pylint: disable=unused-argument
        """called when we (re)connect to the radio"""
        # defaults to broadcast, specify a destination ID if you wish
        print("connected")



def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("-k", "--kafka-broker", help="Kafka Broker (bootstrap.servers).")
    parser.add_argument("--topic", help="Kafka Topic for Mesh Packages in proto.")
    parser.add_argument("-v", "--verbose", help="more verbose.", action="store_true")
    parser.add_argument("--host", help="Meshtastic radio.")

    args = parser.parse_args()

    if args.verbose:
        logging.basicConfig(level=logging.DEBUG)
    else:
        logging.basicConfig(level=logging.WARN)

    if args.kafka_broker:
        meshHandler = MeshHandler(useKafka=True, bootstrap_servers=args.kafka_broker, verbose=args.verbose)
    else:
      meshHandler = MeshHandler()

    if args.topic:
        meshHandler.topic(args.topic)


    pub.subscribe(meshHandler.onReceive, "meshtastic.receive")
    pub.subscribe(meshHandler.onConnection, "meshtastic.connection.established")

    try:
        iface = None

        if args.host:
            iface = meshtastic.tcp_interface.TCPInterface(args.host)
        else:            
            iface = meshtastic.serial_interface.SerialInterface()

        if iface.nodes:
            for n in iface.nodes.values():
                if n["num"] == iface.myInfo.my_node_num:
                    dev_num = n["num"]
                    dev_user = n["user"]["shortName"]
                    dev_model = n["user"]["hwModel"]
                    
                    device_id = f"{dev_num}/{dev_user}/{dev_model}"

                    meshHandler.device_id = device_id

                    print(f"Device: {device_id}")
                    break
        
        try:
            while True:
                time.sleep(1000)
        except KeyboardInterrupt:
            logging.info("Exiting due to keyboard interrupt")
            iface.close()

    except Exception as ex:
        print(f"Error: {ex}")
        sys.exit(1)

if __name__ == "__main__":
    main()
