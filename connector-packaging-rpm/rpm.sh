#!/bin/bash

echo 'connector ALL=(ALL:ALL) ALL' >> /etc/sudoers
groupadd -r connector
useradd -r -g connector connector

if [ -f /etc/connector/configs.json ];
then
  rm /etc/connector/configs_new.json
else
  mv /etc/connector/configs_new.json /etc/connector/configs.json
fi
#echo "Check for configs.json"

if [ -f /etc/connector/connector.conf ];
then
   rm /etc/connector/connector_new.conf
else
  mv /etc/connector/connector_new.conf /etc/connector/connector.conf
fi

mkdir -p /var/log/connector

chown -R :connector /etc/connector
chown -R :connector /var/log/connector

chmod 774 -R /etc/connector
chmod 774 -R /var/log/connector

mv /dev/random /dev/random.real
ln -s /dev/urandom /dev/random

chmod 774 /etc/init.d/iofog-connector

chmod 754 /usr/bin/iofog-connector

chown :connector /usr/bin/iofog-connector

chkconfig --add connector
chkconfig connector on

ln -sf /usr/bin/iofog-connector /usr/local/bin/iofog-connector
