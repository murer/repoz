#!/bin/bash -x

gcutil --project cloudcontainerz deleteinstance repoz -f --zone us-central1-a --delete_boot_pd
gcutil --project cloudcontainerz deletedisk repoz -f --zone us-central1-a



