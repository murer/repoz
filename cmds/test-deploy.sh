#!/bin/bash -xe

#<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
#  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
#  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
#                      http://maven.apache.org/xsd/settings-1.0.0.xsd">
#  <servers>
#	<server>
#		<id>repoz</id>
#		<username>test</username>
#		<password>test123</password>
#	</server>
# </servers>
#</settings>



mvn deploy -Drepoz.dist.snapshot=https://repoz.dextra.com.br/repoz/r/test/snapshots -Dmaven.test.skip.exec -Dmaven.deploy.skip=false


