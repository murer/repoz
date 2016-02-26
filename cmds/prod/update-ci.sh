#!/bin/bash -xe

EXT_IP=130.211.143.253
FILENAME=repoz-web/target/repoz-web.war

scp -i ~/.ssh/repozci -o ConnectTimeout=15 -o UserKnownHostsFile=/dev/null -o CheckHostIP=no -o StrictHostKeyChecking=no "$FILENAME" "repoz@$EXT_IP:packs/repoz.war"

ssh -i ~/.ssh/repozci -o ConnectTimeout=15 -o UserKnownHostsFile=/dev/null -o CheckHostIP=no -o StrictHostKeyChecking=no "repoz@$EXT_IP" ./cmds/prod/repoz-install/repoz-update.sh
