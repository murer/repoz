#!/bin/bash -xe

#gcutil --project cloudcontainerz deleteinstance repoz -f --zone us-central1-a --delete_boot_pd
#gcutil --project cloudcontainerz deletedisk repoz -f --zone us-central1-a

gcloud compute instances delete repoz --project cloudcontainerz --delete-disks all --zone us-central1-a -q || true
gcloud compute disks delete repoz --project cloudcontainerz -q || true
