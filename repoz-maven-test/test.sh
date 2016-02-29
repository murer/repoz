#!/bin/bash -xe

rm -rf gen/maven || true
find gen/repository -type d -name appengine-tools-sdk -exec rm -rf '{}' \; || true
mkdir -p gen/files mkdir -p gen/maven || true
ls gen/files/appengine-tools-sdk-1.9.32.jar || wget 'http://repo1.maven.org/maven2/com/google/appengine/appengine-tools-sdk/1.9.32/appengine-tools-sdk-1.9.32.jar' -O 'gen/files/appengine-tools-sdk-1.9.32.jar'



check_version() {
  ls "gen/files/$2" || wget "$3" -O "gen/files/$2"
  cd gen/maven
  tar xzf "../files/$2"
  cd -
  export MAVEN_HOME="$PWD/gen/maven/apache-maven-$1"
  sed "s/gen\/repository/gen\/repository\/repo-$1/g" settings/maven.settings.xml > "$MAVEN_HOME/settings.xml"
  $MAVEN_HOME/bin/mvn -version

  mvn deploy:deploy-file \
      -s "$MAVEN_HOME/settings.xml" \
      -DgroupId=com.murerz.repoz.test \
      -DartifactId=appengine-tools-sdk \
      -Dversion=1.9.32 \
      -Dpackaging=jar \
      -DrepositoryId=repoz \
      -Durl=http://repoz.dextra.com.br/repozix/r/test/repository \
      -Dfile=gen/files/appengine-tools-sdk-1.9.32.jar

  mvn clean install -s "$MAVEN_HOME/settings.xml" -Dmaven.test.skip

  mvn deploy -s "$MAVEN_HOME/settings.xml" -Dmaven.test.skip
}

check_version '3.2.2' 'apache-maven-3.2.2-bin.tar.gz' 'https://archive.apache.org/dist/maven/binaries/apache-maven-3.2.2-bin.tar.gz'
check_version '2.2.1' 'apache-maven-2.2.1-bin.tar.gz' 'https://archive.apache.org/dist/maven/binaries/apache-maven-2.2.1-bin.tar.gz'
