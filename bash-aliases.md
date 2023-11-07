### Bash aliases
```bash

# Git clone with other ssh-key
alias mgc='git clone -c core.sshcommand="/usr/bin/ssh -i /home/nefro/.ssh/mbo"'

alias mji='mvn install -DskipTest=true -Ddockerfile.skip=true'

alias skipper-bash="docker container exec -it skipper /bin/bash"

# Kafka
alias klt="kafka-topics.sh --bootstrap-server localhost:9092 --list"


```
