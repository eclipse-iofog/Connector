#!/usr/bin/env bash
set -e

PASSWORD=$1
USER=$2
IP=$3

sshpass -p ${PASSWORD} ssh -o StrictHostKeyChecking=no ${USER}@${IP} "service iofog-connector stop; sudo apt-get update; sudo apt-get install --only-upgrade -y iofog-connector${ID}; service iofog-connector start"
