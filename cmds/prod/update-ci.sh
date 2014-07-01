#!/bin/bash -xe

EXT_IP=108.59.84.254
FILENAME=repoz-web/target/repoz-web.war

scp -o ConnectTimeout=15 -o UserKnownHostsFile=/dev/null -o CheckHostIP=no -o StrictHostKeyChecking=no -i cmds/prod/repoz-install/keys/id_rsa "$FILENAME" "repoz@$EXT_IP:packs/repoz.war"

ssh -o ConnectTimeout=15 -o UserKnownHostsFile=/dev/null -o CheckHostIP=no -o StrictHostKeyChecking=no -i cmds/prod/repoz-install/keys/id_rsa "repoz@$EXT_IP" ./cmds/prod/repoz-install/repoz-update.sh

