#!/bin/bash -xe

export LC_ALL=en_US.UTF-8
export DEBIAN_FRONTEND=noninteractive

############
adduser --disabled-password --gecos "" repoz || true

mkdir /home/repoz/.ssh || true
ssh-keygen -t rsa -N "" -f /home/repoz/.ssh/id_rsa
cp /home/repoz/.ssh/id_rsa.pub /home/repoz/.ssh/authorized_keys
echo "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQC4OrzpGzVrpyugu3nj1NUmPFKSVe7UcbG3mgS8wq6/58jyh8w6f3QdCPqs1Ms+BilzS5PX5OdMEYTY8AsUoNC7tIP62lkAp5OBL8TYelGpeMwgzERO+BOGc8MRGlwIhQgoJvp+BdZVcGXs85kIMYTelEk40V0Yd9jPHV3tUmX5qX14OLBvzc7nsNjENyGM2j1gTo/XzYtSPOYh3TeLvFjjaTUyDu9GOFnMUu/JHuJTeylcAOHqXMVPsvsA8AArtWUIlOj+kab4OLUF4gMy8vlXhqp59JJXzdgSluREwkwvCAht8EbffuEn8bDuWpfpyz4aR/v6yws3GMFV+SkemR+7 repoz@ci" >> /home/repoz/.ssh/authorized_keys

cd /home/repoz

############
mkdir -p cmds/prod/repoz-install
curl -H 'Metadata-Flavor: Google' 'http://metadata/computeMetadata/v1/instance/attributes/repoz-update' > cmds/prod/repoz-install/repoz-update.sh
chmod +x cmds/prod/repoz-install/repoz-update.sh

############
gsutil -m cp -r gs://cz-repoz-config/repoz-repo .

mkdir -p packs/WEB-INF/classes
if [ ! -f repoz-repo/repoz.properties ]; then
    echo "No repoz.properties found in repoz-repo/repoz.properties"
    exit 1;
fi

############
REPOZ_PASSWORD=$(grep "^repoz\.access\.root=.\+$" repoz-repo/repoz.properties | sed "s/^repoz\.access\.root=\(.\+\)$/\1/g")
if [ "x$REPOZ_PASSWORD" == "x" ]; then
    echo "repoz.access.root not found in repoz-repo/repoz.properties";
    exit 1;
fi
mkdir repoz-repo/repository || true
cp repoz-repo/repoz.properties packs/WEB-INF/classes/repoz.properties

############
echo "America/Sao_Paulo" | sudo tee /etc/timezone
sudo dpkg-reconfigure --frontend noninteractive tzdata
sudo service cron restart

############
sudo apt-get -y update
#sudo apt-get -y upgrade
sudo apt-get -y install vim nmap netcat tcpdump zip pv apache2-utils apache2 curl ntpdate psmisc

############
sudo service ntp stop
sudo ntpdate ntp.ubuntu.com
sudo service ntp start
sudo service cron restart
date

cd /opt

############
gsutil cp gs://cz-repoz/pub/jdk/oracle/jdk-7u67-linux-x64.tar.gz jdk.tar.gz

tar xzf jdk.tar.gz
sudo ln -s jdk1.7.0_67 jdk

tee /etc/bash.bashrc.repoz <<-EOF
export JAVA_HOME=/opt/jdk
export PATH=\$JAVA_HOME/bin:$PATH
EOF

echo "# repoz" | tee -a /etc/bash.bashrc
echo "source /etc/bash.bashrc.repoz" | tee -a /etc/bash.bashrc

source /etc/bash.bashrc.repoz

java -version

############
a2enmod proxy proxy_http ssl headers rewrite
htpasswd -bc /etc/apache2/passwd root "$REPOZ_PASSWORD"

rm /etc/apache2/sites-enabled/000-default
tee /etc/apache2/sites-available/repoz <<-EOF
<Location /home/repoz>
    AuthType Basic
    AuthName "repoz"
    AuthUserFile /etc/apache2/passwd
    Require user root
</Location>
<VirtualHost *:80>
    RewriteEngine on
    RewriteRule ^/repoz/access - [F]
    #RewriteRule ^/repoz/panel\.html https://%{HTTP_HOST}/repoz/panel.html

    <Location /repoz>
        ProxyPass http://localhost:8080/repoz
        ProxyPassReverse http://localhost:8080/repoz
        RequestHeader unset Authorization
        RequestHeader set X-Repoz-Schema "http"
    </Location>
    <Location /repozix/r>
        ProxyPass http://localhost:8080/repoz/r
        ProxyPassReverse http://localhost:8080/repoz/r
        RequestHeader set X-Repoz-Schema "http"
    </Location>
    <Location /repozix/d>
        ProxyPass http://localhost:8080/repoz/d
        ProxyPassReverse http://localhost:8080/repoz/d
        RequestHeader set X-Repoz-Schema "http"
    </Location>

    DocumentRoot /var/www
    <Directory />
        Options FollowSymLinks
        AllowOverride None
    </Directory>
    Alias /home/repoz "/home/repoz"
    <Directory /home/repoz>
        Options Indexes FollowSymLinks MultiViews
        AllowOverride None
        Order allow,deny
        allow from all
    </Directory>
</VirtualHost>
EOF

tee /etc/apache2/sites-available/repoz-ssl <<-EOF
<IfModule mod_ssl.c>
<VirtualHost _default_:443>
    RewriteEngine on
    RewriteRule ^/repoz/docs\.html http://%{HTTP_HOST}/repoz/docs.html
    RewriteRule ^/repoz/index\.html http://%{HTTP_HOST}/repoz/index.html
    RewriteRule ^/repoz/$ http://%{HTTP_HOST}/repoz/

    <Location /repoz>
        ProxyPass http://localhost:8080/repoz
        ProxyPassReverse http://localhost:8080/repoz
        RequestHeader set X-Repoz-Schema "https"
    </Location>

    ServerAdmin webmaster@localhost

    DocumentRoot /var/www
    <Directory />
        Options FollowSymLinks
        AllowOverride None
    </Directory>
    <Directory /var/www/>
        Options Indexes FollowSymLinks MultiViews
        AllowOverride None
        Order allow,deny
        allow from all
    </Directory>

    ScriptAlias /cgi-bin/ /usr/lib/cgi-bin/
    <Directory "/usr/lib/cgi-bin">
        AllowOverride None
        Options +ExecCGI -MultiViews +SymLinksIfOwnerMatch
        Order allow,deny
        Allow from all
    </Directory>

    ErrorLog ${APACHE_LOG_DIR}/error.log

    LogLevel warn

    CustomLog ${APACHE_LOG_DIR}/ssl_access.log combined

    SSLEngine on
    SSLProtocol all -SSLv2
    SSLCipherSuite ALL:!ADH:!EXPORT:!SSLv2:RC4+RSA:+HIGH:+MEDIUM

    SSLCertificateFile /home/repoz/repoz-repo/ssl/repoz.dextra.com.br.pem
    SSLCertificateKeyFile /home/repoz/repoz-repo/ssl/repoz.dextra.com.br.key
    SSLCertificateChainFile /home/repoz/repoz-repo/ssl/sub.class1.server.ca.pem

    <FilesMatch "\.(cgi|shtml|phtml|php)$">
        SSLOptions +StdEnvVars
    </FilesMatch>
    <Directory /usr/lib/cgi-bin>
        SSLOptions +StdEnvVars
    </Directory>

    BrowserMatch "MSIE [2-6]" \
        nokeepalive ssl-unclean-shutdown \
        downgrade-1.0 force-response-1.0
    BrowserMatch "MSIE [17-9]" ssl-unclean-shutdown

</VirtualHost>
</IfModule>
EOF

cp /var/www/index.html /var/www/index.html.old
tee /var/www/index.html <<-EOF
<html><head><title>Repoz</title></head><body onload="location='repoz';"></body></html>
EOF
ln -s /etc/apache2/sites-available/repoz /etc/apache2/sites-enabled/repoz
ln -s /etc/apache2/sites-available/repoz-ssl /etc/apache2/sites-enabled/repoz-ssl
service apache2 restart

############
#./certbot-auto certonly --staging --agree-tos -n --register-unsafely-without-email --webroot -w /var/www/ -d repoz.dextra.com.br

#    SSLCertificateFile /etc/letsencrypt/live/repoz.dextra.com.br/cert.pem
#    SSLCertificateKeyFile /etc/letsencrypt/live/repoz.dextra.com.br/privkey.pem
#    SSLCertificateChainFile /etc/letsencrypt/live/repoz.dextra.com.br/chain.pem

############
cd /home/repoz
mkdir opt
cd opt
gsutil cp gs://cz-repoz/pub/jboss/jboss-as-7.1.1.Final.zip jboss.zip
unzip jboss.zip
ln -s jboss-as-7.1.1.Final jboss

chown -R repoz:repoz /home/repoz

sleep 3

echo "install-instance done"
