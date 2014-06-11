#!/bin/bash -xe

mkdir repoz-repo

sudo mount /dev/disk/by-id/google-repoz-repository repoz-repo

tee opt/jboss/standalone/configuration/repoz.properties <<-EOF
repoz.access.root=1q2w3e4r
repoz.rawfilesytem.base=/home/repoz/repoz-repo/repository
EOF







