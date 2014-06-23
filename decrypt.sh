#!/bin/bash -ex

find -name "*.repoz-crypt" | while read k; do 
	echo "decrypt: $k";
	openssl enc -des-ede3-cbc -salt -in "$k" -out "$(echo "$k" | sed "s/\.repoz-crypt$//g")" -d -pass pass:$REPOZSECRET; 
done

