#!/bin/bash -xe

mkdir -p repoz-repo packs/WEB-INF/classes 

sudo mount /dev/disk/by-id/google-repoz-repository repoz-repo

mkdir /home/repoz/repoz-repo/repository | cat

tee packs/WEB-INF/classes/repoz.properties <<-EOF
repoz.access.root=1q2w3e4r
repoz.rawfilesytem.base=/home/repoz/repoz-repo/repository
EOF







