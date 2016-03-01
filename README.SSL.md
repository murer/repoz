# HTTPS

https://www.startssl.com/

login: webmaster@dextra.com.br

validate domain repoz.dextra.com.br

```shell
# generate keys
./cmds/prod/ssl/generate-key.sh

# upate startssl certificate
# download certificate from startssl:
# repoz.dextra.com.br.pem
# cp repoz.dextra.com.br.pem target/ssl

# update repoz configs
./cmds/prod/ssl/update-key.sh

# recreate repoz instance
./cmds/prod/stop.sh
./cmds/prod/start.sh

# deploy application
git checkout repoz-x.x.x
./cmds/update.sh
```










