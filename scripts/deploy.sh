#!/usr/bin/env bash

PASSWORD=$1
USER=$2
IP=$3

sshpass -p ${PASSWORD} ssh -o StrictHostKeyChecking=no ${USER}@${IP}
    "service iofog-connector stop"
shpass -p ${PASSWORD} scp -o StrictHostKeyChecking=no daemon/target/iofog-connector-daemon-jar-with-dependencies.jar
    ${USER}@${IP}:/usr/bin/iofog-connectord.jar
sshpass -p ${PASSWORD} scp -o StrictHostKeyChecking=no client/target/iofog-connector-client-jar-with-dependencies.jar
    $${USER}@${IP}:/usr/bin/iofog-connector.jar
sshpass -p ${PASSWORD} ssh -o StrictHostKeyChecking=no ${USER}@${IP}
    "service iofog-connector start"