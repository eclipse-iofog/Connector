FROM iofog/java-ubuntu-x86:8u211

COPY iofog-connector-packaging/etc /etc
COPY iofog-connector-packaging/usr /usr
COPY daemon/target/iofog-connector-daemon-jar-with-dependencies.jar /usr/bin/iofog-connectord.jar
COPY client/target/iofog-connector-client-jar-with-dependencies.jar /usr/bin/iofog-connector.jar

RUN apt-get update && \
    apt-get install -y sudo && \
    useradd -r -U -s /usr/bin/nologin iofog-connector && \
    usermod -aG root,sudo iofog-connector && \
    mv /etc/iofog-connector/configs_new.json /etc/iofog-connector/configs.json && \
    mv /etc/iofog-connector/iofog-connector_new.conf /etc/iofog-connector/iofog-connector.conf && \
    mkdir -p /var/log/iofog-connector && \
    chown -R :iofog-connector /etc/iofog-connector && \
    chown -R :iofog-connector /var/log/iofog-connector && \
    chmod 774 -R /etc/iofog-connector && \
    chmod 774 -R /var/log/iofog-connector && \
    mv /dev/random /dev/random.real && \
    ln -s /dev/urandom /dev/random && \
    chmod 774 /etc/init.d/iofog-connector && \
    chmod 754 /usr/bin/iofog-connector && \
    chown :iofog-connector /usr/bin/iofog-connector && \
    update-rc.d iofog-connector defaults && \
    ln -sf /usr/bin/iofog-connector /usr/local/bin/iofog-connector && \
    echo "service iofog-connector start && tail -f /dev/null" >> /start.sh

CMD [ "sh", "/start.sh" ]
