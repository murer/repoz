#!/bin/bash -xe

#rm -rf gen/repository || true
mkdir -p gen/files || true

ls gen/files/appengine-tools-sdk-1.9.32.jar || wget 'http://repo1.maven.org/maven2/com/google/appengine/appengine-tools-sdk/1.9.32/appengine-tools-sdk-1.9.32.jar' -O 'gen/files/appengine-tools-sdk-1.9.32.jar'

mvn deploy:deploy-file \
    -s settings/maven.settings.xml \
    -DgroupId=com.murerz.repoz.test \
    -DartifactId=appengine-tools-sdk \
    -Dversion=1.9.32 \
    -Dpackaging=jar \
    -DrepositoryId=repoz \
    -Durl=http://repoz.dextra.com.br/repozix/r/test/repository \
    -Dfile=gen/files/appengine-tools-sdk-1.9.32.jar

mvn clean install -s settings/maven.settings.xml -Dmaven.test.skip

mvn deploy -s settings/maven.settings.xml
