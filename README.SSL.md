# HTTPS

https://www.startssl.com/

login: webmaster@dextra.com.br

validate domain repoz.dextra.com.br

```shell
# generate keys
./cmds/prod/ssl/generate-key.sh

# update repoz configs
./cmds/prod/ssl/update-key.sh

# recreate repoz instance
./cmds/prod/stop.sh
./cmds/prod/start.sh
```










