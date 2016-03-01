#!/bin/bash -xe

rm -rf target/ssl || true
mkdir -p target/ssl

cd target/ssl
openssl genrsa -out repoz.dextra.com.br.key 4096
openssl req -new -sha256 -key repoz.dextra.com.br.key -out repoz.dextra.com.br.csr -subj "/C=BR/ST=Sao Paulo/L=Campinas/O=Dextra/OU=Repoz/CN=repoz.dextra.com.br"
wget http://www.startssl.com/certs/sub.class1.server.ca.pem
cd -
