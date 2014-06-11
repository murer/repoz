#!/bin/bash -xe

source /etc/bash.bashrc.repoz

cat > packs/repoz.war

cd packs

jar uvf repoz.war WEB-INF/classes/repoz.properties

cd -

killall java | cat 
sleep 3
killall -9 java | cat
sleep 3;

rm -rf opt/jboss/standalone/tmp | cat
rm -rf opt/jboss/standalone/log | cat
rm -rf opt/jboss/standalone/data | cat
rm -rf opt/jboss/standalone/work | cat
rm -rf opt/jboss/standalone/deployments/repoz* | cat

cp packs/repoz.war opt/jboss/standalone/deployments/repoz.war

cd opt/jboss/bin
(./standalone.sh 1> /home/repoz/jboss.out 2>&1 &)

sleep 3;


