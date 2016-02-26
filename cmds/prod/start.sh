#!/bin/bash -xe

tar czf - cmds/prod/repoz-install | gsutil cp - gs://cz-repoz-config/repoz-install.tar.gz

gcloud compute instances create repoz \
    --project cloudcontainerz \
    --boot-disk-auto-delete \
    --zone us-central1-a \
    --machine-type n1-standard-1 \
    --image debian \
    --address 130.211.143.253 \
		--metadata-from-file startup-script=cmds/prod/repoz-install/install.sh \
    --scopes 'https://www.googleapis.com/auth/devstorage.read_only'

while ! gcloud compute ssh repoz --project cloudcontainerz --zone us-central1-a --command whoami; do sleep 2; done

gcloud compute ssh repoz --project cloudcontainerz --zone us-central1-a --command 'tail -f /var/log/startupscript.log'
