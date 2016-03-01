#!/bin/bash -xe

cd target/ssl
gsutil -m rm -r gs://cz-repoz-config/repoz-repo/ssl
gsutil -m cp repoz.dextra.com.br.csr repoz.dextra.com.br.key sub.class1.server.ca.pem gs://cz-repoz-config/repoz-repo/ssl

cat repoz.dextra.com.br.csr

cd -
