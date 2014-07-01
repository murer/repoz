#!/bin/bash -xe

FILENAME="$1"
#CAT=cat

if [ "x$1" == "x" ]; then
	FILENAME=repoz-web/target/repoz-web.war
fi

#if pv --help 1> /dev/null 2>&1 ; then
#	CAT=pv;
#fi;

#$CAT "$FILENAME" | cmds/prod/connect.sh ./cmds/prod/repoz-install/repoz-update.sh

cmds/prod/upload.sh "$FILENAME" packs/repoz.war

cmds/prod/connect.sh ./cmds/prod/repoz-install/repoz-update.sh





