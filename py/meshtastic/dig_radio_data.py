import sys
import time
import logging
import base64
import datetime
from pubsub import pub


import meshtastic
import meshtastic.serial_interface

def onReceive(packet, interface, topic=pub.AUTO_TOPIC):  # pylint: disable=unused-argument
    """called when a packet arrives"""
    print("==============")
    stamp = datetime.datetime.now().astimezone().isoformat()
    
    keys_list = list(packet.keys())
    for key in keys_list:
        if key == 'raw':
            bin: bytes = packet['raw'].SerializeToString()
            print(f"[BASE64]{stamp},{base64.b64encode(bin).decode('utf-8')}")
            # hex = packet['raw'].SerializeToString().hex()
            # print(f"[HEX]: {hex}")
        logging.info(f"{key} : {type(packet[key])} => {packet[key]}")
    
    #print(f"Received: {hex}")


def onConnection(interface, topic=pub.AUTO_TOPIC):  # pylint: disable=unused-argument
    """called when we (re)connect to the radio"""
    # defaults to broadcast, specify a destination ID if you wish
    print("connected")




def main():
    logging.basicConfig(level=logging.WARN)

    pub.subscribe(onReceive, "meshtastic.receive")
    pub.subscribe(onConnection, "meshtastic.connection.established")

    try:
        iface = meshtastic.serial_interface.SerialInterface()

        if iface.nodes:
            for n in iface.nodes.values():
                if n["num"] == iface.myInfo.my_node_num:
                    print(n["user"]["hwModel"])
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
