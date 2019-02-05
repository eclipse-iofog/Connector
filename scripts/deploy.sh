#!/usr/bin/env bash

PASSWORD=$1
USER=$2
IP=$3

sshpass -p ${PASSWORD} ssh -p StrictHostKeyChecking=no ${USER}@${IP}
    "service iofog-connector stop sudo apt-get install --only-upgrade -y iofog-connector${DEV}";
if [ "$TRAVIS_BRANCH" == "develop" ]; then sshpass -p ${PASSWORD} scp -p StrictHostKeyChecking=no daemon/target/iofog-connector-daemon-jar-with-dependencies.jar
    ${USER}@${IP}:/usr/bin/iofog-connectord.jar; sshpass -p ${PASSWORD} scp -p StrictHostKeyChecking=no client/target/iofog-connector-client-jar-with-dependencies.jar
    ${USER}@${IP}:/usr/bin/iofog-connector.jar; fi
sshpass -p ${PASSWORD} ssh -p StrictHostKeyChecking=no ${USER}@${IP}
    "service iofog-connector start"
