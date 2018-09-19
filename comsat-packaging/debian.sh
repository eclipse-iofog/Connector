#!/bin/bash

useradd -r -U -s /usr/bin/nologin comsat
usermod -aG admin,sudo comsat
echo "Added comsat user and group"

if [ -f /etc/comsat/configs.json ];
then
   rm /etc/comsat/configs_new.json
else
  mv /etc/comsat/configs_new.json /etc/comsat/configs.json
fi

mkdir -p /var/log/comsat

chown -R :comsat /etc/comsat
chown -R :comsat /var/log/comsat

chmod 774 -R /etc/comsat
chmod 774 -R /var/log/comsat

mv /dev/random /dev/random.real
ln -s /dev/urandom /dev/random

chmod 774 /etc/init.d/comsat

chmod 754 /usr/bin/comsat

chown :comsat /usr/bin/comsat

update-rc.d comsat defaults

ln -sf /usr/bin/comsat /usr/local/bin/comsat

