#!/bin/bash -xe

useradd repoz

mkdir /home/repoz/.ssh || true
ssh-keygen -t rsa -N "" -f /home/repoz/.ssh/id_rsa
cp /home/repoz/.ssh/id_rsa.pub /home/repoz/.ssh/authorized_keys

cd /home/repoz

gsutil cp gs://cz-repoz-config/repoz-install.tar.gz .
tar xzf repoz-install.tar.gz
find cmds -name '*.sh' -exec chmod -v +x '{}' \;
./cmds/prod/repoz-install/install-instance.sh

chown -R repoz:repoz /home/repoz

sleep 3
