#!/bin/bash -xe

((./cmds/prod/repoz-install/install-instance.sh 1> install.log 2>&1; cp install.log repoz-repo/install.log | cat) &)

sleep 3

