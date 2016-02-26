#!/bin/bash -e

#EXT_IP=130.211.143.253
#while [ "x$EXT_IP" == "x" ]; do
#	EXT_IP="$(gcutil --project cloudcontainerz listinstances  --zone us-central1-a  --filter="name eq 'repoz'" | cut -d"|" -f6 | tail -n +4 | head -n 1| sed "s/\s\+//g")"
#done

#ssh -o ConnectTimeout=15 -o UserKnownHostsFile=/dev/null -o CheckHostIP=no -o StrictHostKeyChecking=no repoz@$EXT_IP $*

gcloud compute ssh repoz --project cloudcontainerz --zone us-central1-a
