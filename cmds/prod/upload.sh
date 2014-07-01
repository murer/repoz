#!/bin/bash -e

EXT_IP=108.59.84.254
#while [ "x$EXT_IP" == "x" ]; do
#	EXT_IP="$(gcutil --project docs-manager listinstances  --zone us-central1-a  --filter="name eq 'repoz-prod'" | cut -d"|" -f6 | tail -n +4 | head -n 1| sed "s/\s\+//g")"
#done

scp -o ConnectTimeout=15 -o UserKnownHostsFile=/dev/null -o CheckHostIP=no -o StrictHostKeyChecking=no "$1" "repoz@$EXT_IP:$2"

