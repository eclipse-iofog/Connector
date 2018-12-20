#!/usr/bin/env bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null && pwd )"
PUSH_YANK_LIST="$(bash ${DIR}/pushyank.sh)"

echo "${PUSH_YANK_LIST}"

export VERSION=`xml_grep --cond='project/version' pom.xml --text_only`
sshpass -p $STAGE_MACHINE_PASSWORD ssh -o StrictHostKeyChecking=no $STAGE_MACHINE_USERNAME@$STAGE_MACHINE_IP \
      "rm -rf /iofog-connector-packaging-rpm/*; rm -rf /iofog-connector-packaging/*;"
sshpass -p $STAGE_MACHINE_PASSWORD scp -o StrictHostKeyChecking=no -r iofog-connector-packaging-rpm/* \
      $STAGE_MACHINE_USERNAME@$STAGE_MACHINE_IP:/iofog-connector-packaging-rpm/
sshpass -p $STAGE_MACHINE_PASSWORD scp -r iofog-connector-packaging/* $STAGE_MACHINE_USERNAME@$STAGE_MACHINE_IP:/iofog-connector-packaging/
sshpass -p $STAGE_MACHINE_PASSWORD scp client/target/iofog-connector-client-jar-with-dependencies.jar \
      $STAGE_MACHINE_USERNAME@$STAGE_MACHINE_IP:/iofog-connector-packaging/usr/bin/iofog-connector.jar
sshpass -p $STAGE_MACHINE_PASSWORD scp daemon/target/iofog-connector-daemon-jar-with-dependencies.jar \
      $STAGE_MACHINE_USERNAME@$STAGE_MACHINE_IP:/iofog-connector-packaging/usr/bin/iofog-connectord.jar
sshpass -p $STAGE_MACHINE_PASSWORD scp client/target/iofog-connector-client-jar-with-dependencies.jar \
      $STAGE_MACHINE_USERNAME@$STAGE_MACHINE_IP:/iofog-connector-packaging-rpm/usr/bin/iofog-connector.jar
sshpass -p $STAGE_MACHINE_PASSWORD scp daemon/target/iofog-connector-daemon-jar-with-dependencies.jar \
      $STAGE_MACHINE_USERNAME@$STAGE_MACHINE_IP:/iofog-connector-packaging-rpm/usr/bin/iofog-connectord.jar
sshpass -p $STAGE_MACHINE_PASSWORD ssh -o StrictHostKeyChecking=no $STAGE_MACHINE_USERNAME@$STAGE_MACHINE_IP \
      "${PUSH_YANK_LIST}"