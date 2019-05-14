#!/bin/bash -xe

gcloud compute instances create repoz \
    --project cloudcontainerz \
    --boot-disk-auto-delete \
    --zone us-central1-a \
    --machine-type n1-standard-1 \
    --image-project debian-cloud \
    --image-family debian-9 \
    --address 130.211.143.253 \
		--metadata-from-file 'startup-script=cmds/prod/repoz-install/install-instance.sh,repoz-update=cmds/prod/repoz-install/repoz-update.sh' \
    --scopes 'https://www.googleapis.com/auth/devstorage.read_only'

while ! gcloud compute ssh repoz --project cloudcontainerz --zone us-central1-a --command whoami; do sleep 2; done

gcloud compute ssh repoz --project cloudcontainerz --zone us-central1-a --command 'tail -n 1000 -f /var/log/startupscript.log'
