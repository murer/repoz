#!/bin/bash -xe

EXT_IP=130.211.143.253
FILENAME=repoz-web/target/repoz-web.war

chmod 600 cmds/prod/repoz-install/keys/id_rsa 

scp -o ConnectTimeout=15 -o UserKnownHostsFile=/dev/null -o CheckHostIP=no -o StrictHostKeyChecking=no -i cmds/prod/repoz-install/keys/id_rsa "$FILENAME" "repoz@$EXT_IP:packs/repoz.war"

ssh -o ConnectTimeout=15 -o UserKnownHostsFile=/dev/null -o CheckHostIP=no -o StrictHostKeyChecking=no -i cmds/prod/repoz-install/keys/id_rsa "repoz@$EXT_IP" ./cmds/prod/repoz-install/repoz-update.sh

