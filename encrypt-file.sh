#!/bin/bash

openssl enc -aes-256-cbc -salt -in "$1" -out "$1.repoz-crypt" -pass "pass:$REPOZSECRET";

