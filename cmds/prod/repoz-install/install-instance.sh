#!/bin/bash -xe

export LC_ALL=en_US.UTF-8
export DEBIAN_FRONTEND=noninteractive

############
echo "America/Sao_Paulo" | sudo tee /etc/timezone
sudo dpkg-reconfigure --frontend noninteractive tzdata
sudo service cron restart

############
mkdir -p repoz-repo packs/WEB-INF/classes 
sudo mount /dev/disk/by-id/google-repoz-repository repoz-repo
if [ ! -f repoz-repo/repoz.properties ]; then
	echo "No repoz.properties found in repoz-repo/repoz.properties"
	exit 1;
fi
mkdir repoz-repo/repository | cat
cp repoz-repo/repoz.properties packs/WEB-INF/classes/repoz.properties

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
sudo wget http://storage.googleapis.com/dextra-pdoc-pub/repo/ext/br/com/portaldedocumentos/ext/jdk/7u45-linux-x64/jdk-7u45-linux-x64.zip -O jdk.zip

sudo unzip jdk.zip
sudo ln -s jdk1.7.0_45 jdk

sudo tee /etc/bash.bashrc.repoz <<-EOF
export JAVA_HOME=/opt/jdk
export PATH=\$JAVA_HOME/bin:$PATH
EOF

echo "# repoz" | sudo tee -a /etc/bash.bashrc
echo "source /etc/bash.bashrc.repoz" | sudo tee -a /etc/bash.bashrc

source /etc/bash.bashrc.repoz

java -version

############
sudo a2enmod proxy proxy_http

sudo htpasswd -bc /etc/apache2/passwd root 1q2w3e4r

sudo rm /etc/apache2/sites-enabled/000-default
sudo tee /etc/apache2/sites-available/repoz <<-EOF
<Location /repoz>
    ProxyPass http://localhost:8080/repoz
    ProxyPassReverse http://localhost:8080/repoz
</Location>
<Location /home/repoz>
    AuthType Basic
    AuthName "repoz"
    AuthUserFile /etc/apache2/passwd
    Require user root
</Location>
<VirtualHost *:80>
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
sudo ln -s /etc/apache2/sites-available/repoz /etc/apache2/sites-enabled/repoz
sudo service apache2 restart

cd -

############
mkdir opt
cd opt
wget http://storage.googleapis.com/dextra-pdoc-pub/repo/ext/br/com/portaldedocumentos/ext/jboss-as/7.1.1.Final/jboss-as-7.1.1.Final.zip -O jboss.zip
unzip jboss.zip
ln -s jboss-as-7.1.1.Final jboss

cd -

echo "install-instance done"




