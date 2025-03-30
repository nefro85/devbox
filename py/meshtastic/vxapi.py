from fastapi import FastAPI
from fastapi.encoders import jsonable_encoder
from pydantic import BaseModel

import os

app = FastAPI()

class Message(BaseModel):
    chan: int = 1
    text: str


class SendResult(BaseModel):
    status: str = "success"
    out: list[str]


def exec(command: str) -> SendResult:
    lines = os.popen(command).readlines()
    res = SendResult(out=lines)
    return res

@app.get("/meshtastic/message")
async def send_message(text: str, chan: int = 1):
    cmd = f"meshtastic --host $MESH_HOST --sendtext '{text}' --ch-index {chan}"
    
    res = exec(cmd)

    return jsonable_encoder(res)


@app.put("/meshtastic/message")
async def send_message(msg: Message):
    cmd = f"meshtastic --host $MESH_HOST --sendtext '{msg.text}' --ch-index {msg.chan}"
    res = exec(cmd)    

    return jsonable_encoder(res)

