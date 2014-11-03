#!/bin/bash -xe 

mvn mycontainer:start -Drepoz.google.auth.disabled=true -Dgcs=cz-repoz-test

