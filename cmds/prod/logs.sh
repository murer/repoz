#!/bin/bash -e

EXT_IP=130.211.143.253
#while [ "x$EXT_IP" == "x" ]; do
#	EXT_IP="$(gcutil --project cloudcontainerz listinstances  --zone us-central1-a  --filter="name eq 'repoz'" | cut -d"|" -f6 | tail -n +4 | head -n 1| sed "s/\s\+//g")"
#done

gcloud compute ssh repoz --command "sudo find /home/repoz -name '*.log' | xargs tail -n 100 -f"
