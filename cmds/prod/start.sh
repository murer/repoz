#!/bin/bash -xe

#Check ssh key
if [ ! -f "$HOME/.ssh/id_rsa.pub" ]; then 
	echo "$HOME/.ssh/id_rsa.pub not found";
	exit 1;
fi

# Create root disk if it does not exist
#if ! gcutil --project cloudcontainerz listdisks --zone us-central1-a --filter="name eq 'repoz-root'" | grep repoz-root; then
#	gcutil --project cloudcontainerz adddisk repoz-root --size_gb 30 --zone us-central1-a --source_image projects/debian-cloud/global/images/debian-7-wheezy-v20140606
#fi;

# Create repo disk if it does not exist
#if ! gcutil --project cloudcontainerz listdisks --zone us-central1-a --filter="name eq 'repoz-prod'" | grep repoz-prod; then
#	gcutil --project cloudcontainerz adddisk repoz-prod --size_gb 30 --zone us-central1-a
#fi;

if ! gcutil --project cloudcontainerz listinstances --zone us-central1-a --filter="name eq 'repoz'" | grep repoz-prod; then
	gcutil --project cloudcontainerz addinstance repoz --zone us-central1-a --machine_type n1-standard-1 --image debian --auto_delete_boot_disk  --external_ip_address=130.211.143.253 "--authorized_ssh_keys=repoz:$HOME/.ssh/id_rsa.pub" --service_account_scopes "https://www.googleapis.com/auth/devstorage.read_only"
fi

while ! cmds/prod/connect.sh pwd; do
	sleep 3;
done

tar czf - cmds/prod/repoz-install | cmds/prod/connect.sh bash -xc "cat | tar xzvf -"

cmds/prod/connect.sh "find cmds -name '*.sh' -exec chmod -v +x '{}' \;"

cmds/prod/connect.sh ./cmds/prod/repoz-install/install.sh

cmds/prod/connect.sh tail -f install.log













