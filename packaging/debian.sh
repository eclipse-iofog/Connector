#!/bin/bash

useradd -r -U -s /usr/bin/nologin iofog-connector
usermod -aG admin,sudo iofog-connector
echo "Added iofog-connector user and group"

if [ -f /etc/iofog-connector/configs_new.json ];
then
  if [ -f /etc/iofog-connector/configs.json ];
  then
    rm /etc/iofog-connector/configs_new.json
  else
    mv /etc/iofog-connector/configs_new.json /etc/iofog-connector/configs.json
  fi
fi

if [ -f /etc/iofog-connector/iofog-connector_new.conf ];
then
  if [ -f /etc/iofog-connector/iofog-connector.conf ];
  then
    rm /etc/iofog-connector/iofog-connector_new.conf
  else
    mv /etc/iofog-connector/iofog-connector_new.conf /etc/iofog-connector/iofog-connector.conf
  fi
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

update-rc.d iofog-connector defaults

ln -sf /usr/bin/iofog-connector /usr/local/bin/iofog-connector

