#!/bin/bash -xe

#Check ssh key
if [ ! -f gen/prod/id_rsa.pub ]; then 
	echo "gen/prod/id_rsa.pub not found";
	exit 1;
fi

# Create root disk if it does not exist
if ! gcutil --project docs-manager listdisks --zone us-central1-a --filter="name eq 'repoz-root'" | grep repoz-root; then
	gcutil --project docs-manager adddisk repoz-root --size_gb 30 --zone us-central1-a --source_image projects/debian-cloud/global/images/debian-7-wheezy-v20140606
fi;

# Create repo disk if it does not exist
if ! gcutil --project docs-manager listdisks --zone us-central1-a --filter="name eq 'repoz-prod'" | grep repoz-prod; then
	gcutil --project docs-manager adddisk repoz-prod --size_gb 200	 --zone us-central1-a # --source_image projects/debian-cloud/global/images/debian-7-wheezy-v20131120 
fi;

if ! gcutil --project docs-manager listinstances --zone us-central1-a --filter="name eq 'repoz-prod'" | grep repoz-prod; then
	gcutil --project docs-manager addinstance repoz-prod --zone us-central1-a --machine_type n1-standard-1 --disk=repoz-root,mode=READ_WRITE,boot --external_ip_address=108.59.84.254 --disk=repoz-prod,deviceName=repoz-repository,mode=READ_WRITE --authorized_ssh_keys=repoz:gen/prod/id_rsa.pub
fi

while ! cmds/prod/connect.sh pwd; do
	sleep 3;
done

tar czf - cmds/prod/repoz-install | cmds/prod/connect.sh bash -xc "cat | tar xzvf -"

cmds/prod/connect.sh "find cmds -name '*.sh' -exec chmod -v +x '{}' \;"

cmds/prod/connect.sh ./cmds/prod/repoz-install/install.sh

cmds/prod/connect.sh tail -f install.log













