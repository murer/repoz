#!/bin/bash -x

gcutil --project cloudcontainerz deleteinstance repoz-prod -f --zone us-central1-a --nodelete_boot_pd
gcutil --project cloudcontainerz deletedisk repoz-root -f --zone us-central1-a



