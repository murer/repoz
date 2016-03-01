# HTTPS

https://www.startssl.com/

login: webmaster@dextra.com.br

validate domain repoz.dextra.com.br

create private key and csr

```shell
openssl genrsa -out repoz.dextra.com.br.key 4096
openssl req -new -sha256 -key repoz.dextra.com.br.key -out repoz.dextra.com.br.csr
wget http://www.startssl.com/certs/sub.class1.server.ca.pem
```


