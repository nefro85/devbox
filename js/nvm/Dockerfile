# FROM ubuntu:latest
# RUN set -ex; \
#   apt update; \
#   apt install -y curl; \
#   rm -rf /var/lib/apt/lists/*;

# # Use bash for the shell
# SHELL ["/bin/bash", "-o", "pipefail", "-c"]


# WORKDIR /opt/nvm
# # Create a script file sourced by both interactive and non-interactive bash shells
# ENV BASH_ENV /opt/nvm/.bash_env
# RUN touch "${BASH_ENV}"
# RUN echo '. "${BASH_ENV}"' >> ~/.bashrc

# # Download and install nvm
# RUN curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.2/install.sh | PROFILE="${BASH_ENV}" bash
# RUN echo node > .nvmrc
# RUN nvm install

FROM ubuntu:latest
ARG NODE_VERSION=20

# install curl
RUN apt update && apt install curl -y

# install nvm
RUN curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.40.2/install.sh | bash

# set env
ENV NVM_DIR=/root/.nvm

# install node
RUN bash -c "source $NVM_DIR/nvm.sh && nvm install $NODE_VERSION"

# set ENTRYPOINT for reloading nvm-environment
ENTRYPOINT ["bash", "-c", "source $NVM_DIR/nvm.sh && exec \"$@\"", "--"]

# set cmd to bash
CMD ["/bin/bash"] 
