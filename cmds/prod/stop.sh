#!/bin/bash -x

gcutil --project docs-manager deleteinstance repoz-prod -f --zone us-central1-a --nodelete_boot_pd
gcutil --project docs-manager deletedisk repoz-root -f --zone us-central1-a



