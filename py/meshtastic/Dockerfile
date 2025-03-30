FROM python:3.9.2-alpine

# upgrade pip
RUN pip install --upgrade pip
WORKDIR /opt/app

ENV VIRTUAL_ENV=/opt/app/venv \
 PATH="$VIRTUAL_ENV/bin:$PATH"

# python setup
RUN set -ex; \
    python -m venv $VIRTUAL_ENV; \
    pip install meshtastic fastapi[standard];

COPY vxapi.py .
ENV MESH_HOST="192.168.0.170"
ENTRYPOINT [ "sh" , "-c", "fastapi run vxapi.py --host 0.0.0.0 \"$@\"", "--"]
#CMD ["--root-path /vx"]
EXPOSE 8000
