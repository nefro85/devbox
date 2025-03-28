from fastapi import FastAPI
import os

app = FastAPI()


@app.get("/meshtastic/message")
def send_message(text: str, chan: int = 1):
    return os.popen(f"meshtastic --host $MESH_HOST --sendtext '{text}' --ch-index {chan}").read()


