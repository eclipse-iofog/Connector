#!/bin/bash

echo 'comsat ALL=(ALL:ALL) ALL' >> /etc/sudoers
groupadd -r comsat
useradd -r -g comsat comsat

if [ -f /etc/comsat/configs.json ];
then
  rm /etc/comsat/configs_new.json
else
  mv /etc/comsat/configs_new.json /etc/comsat/configs.json
fi
#echo "Check for configs.json"

mkdir /var/log/comsat

chown -R :comsat /etc/comsat
chown -R :comsat /var/log/comsat

chmod 774 -R /etc/comsat
chmod 774 -R /var/log/comsat

mv /dev/random /dev/random.real
ln -s /dev/urandom /dev/random

chmod 774 /etc/init.d/comsat

chmod 754 /usr/bin/comsat

chown :comsat /usr/bin/comsat

chkconfig --add comsat
chkconfig comsat on

ln -sf /usr/bin/comsat /usr/local/bin/comsat
