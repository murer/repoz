#!/bin/bash

openssl enc -des-ede3-cbc -salt -in "$1" -out "$1.repoz-crypt" -pass "pass:$REPOZSECRET";

