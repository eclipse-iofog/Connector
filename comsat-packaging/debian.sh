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
echo "Added log directory"

chown -R :comsat /etc/comsat
chown -R :comsat /var/log/comsat
echo "Changed ownership of directories"

chmod 774 -R /etc/comsat
chmod 774 -R /var/log/comsat
echo "Changed permissions of directories"

mv /dev/random /dev/random.real
ln -s /dev/urandom /dev/random
echo "Moved dev pipes for netty"

chmod 774 /etc/init.d/comsat
echo "Changed permissions on service script"

chmod 754 /usr/bin/comsat
echo "Changed permissions on command line executable file"

chown :comsat /usr/bin/comsat
echo "Changed ownership of command line executable file"

update-rc.d comsat defaults
echo "Registered init.d script"

ln -sf /usr/bin/comsat /usr/local/bin/comsat
echo "Added symlink to comsat command executable"

