#!/bin/bash -xe

FILENAME="$1"

if [ "x$1" == "x" ]; then
	FILENAME=repoz-web/target/repoz-web.war
fi

gcloud compute copy-files --project cloudcontainerz --zone us-central1-a "$FILENAME" repoz@repoz:packs/repoz.war
gcloud compute ssh repoz@repoz --project cloudcontainerz --zone us-central1-a --command './cmds/prod/repoz-install/repoz-update.sh'
