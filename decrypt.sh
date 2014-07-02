#!/bin/bash -e

if [ "x$REPOZSECRET" == "x" ]; then
	echo "export REPOZSECRET to descrypt files";
	exit 1;
fi

find -name "*.repoz-crypt" | while read k; do 
	echo "decrypt: $k";
	openssl enc -aes-256-cbc -salt -in "$k" -out "$(echo "$k" | sed "s/\.repoz-crypt$//g")" -d -pass "pass:$REPOZSECRET"; 
done

