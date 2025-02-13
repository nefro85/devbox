import sys
import time
import logging
import base64
import datetime

from pubsub import pub
from kafka import KafkaProducer


import meshtastic
import meshtastic.serial_interface


class MeshHandler():
    def __init__(self, useKafka:bool = None, topicMesh="meshtastic-from-radio"):
        
        self.useKafka = useKafka
        self.topicName = topicMesh
        self.device_id = None

        if useKafka:
            self.producer = KafkaProducer(bootstrap_servers='lab.syg:9092')

    def onReceive(self, packet, interface, topic=pub.AUTO_TOPIC):  # pylint: disable=unused-argument
        """called when a packet arrives"""
        print("==============")
        stamp = datetime.datetime.now().astimezone().isoformat()
        
        keys_list = list(packet.keys())
        for key in keys_list:
            if key == 'raw':
                bin: bytes = packet['raw'].SerializeToString()
                
                if self.useKafka:
                    self.producer.send(self.topicName, bin)
                
                print(f"[BASE64]{stamp},{base64.b64encode(bin).decode('utf-8')}")
                # hex = packet['raw'].SerializeToString().hex()
                # print(f"[HEX]: {hex}")
            logging.info(f"{key} : {type(packet[key])} => {packet[key]}")
        
        #print(f"Received: {hex}")


    def onConnection(self, interface, topic=pub.AUTO_TOPIC):  # pylint: disable=unused-argument
        """called when we (re)connect to the radio"""
        # defaults to broadcast, specify a destination ID if you wish
        print("connected")



def main():
    logging.basicConfig(level=logging.WARN)

    meshHandler = MeshHandler(useKafka=True)

    pub.subscribe(meshHandler.onReceive, "meshtastic.receive")
    pub.subscribe(meshHandler.onConnection, "meshtastic.connection.established")

    try:
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
