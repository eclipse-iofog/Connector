#!/bin/bash

echo 'iofog-connector ALL=(ALL:ALL) ALL' >> /etc/sudoers
groupadd -r iofog-connector
useradd -r -g iofog-connector iofog-connector

if [ -f /etc/iofog-connector/configs.json ];
then
  rm /etc/iofog-connector/configs_new.json
else
  mv /etc/iofog-connector/configs_new.json /etc/iofog-connector/configs.json
fi
#echo "Check for configs.json"

if [ -f /etc/iofog-connector/iofog-connector.conf ];
then
   rm /etc/iofog-connector/iofog-connector_new.conf
else
  mv /etc/iofog-connector/iofog-connector_new.conf /etc/iofog-connector/iofog-connector.conf
fi

mkdir -p /var/log/iofog-connector

chown -R :iofog-connector /etc/iofog-connector
chown -R :iofog-connector /var/log/iofog-connector

chmod 774 -R /etc/iofog-connector
chmod 774 -R /var/log/iofog-connector

mv /dev/random /dev/random.real
ln -s /dev/urandom /dev/random

chmod 774 /etc/init.d/iofog-connector

chmod 754 /usr/bin/iofog-connector

chown :iofog-connector /usr/bin/iofog-connector

chkconfig --add iofog-connector
chkconfig iofog-connector on

ln -sf /usr/bin/iofog-connector /usr/local/bin/iofog-connector
