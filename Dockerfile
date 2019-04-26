FROM java:8u111-jdk

ARG TAG

RUN curl -s https://packagecloud.io/install/repositories/iofog/iofog-connector/script.deb.sh | bash && \
    apt-get install sudo iofog-connector$TAG && \
    echo "service iofog-connector start && tail -f /dev/null" >> /start.sh && \
    echo '{ "ports": [ "6000-9999", "30000-49999" ], "exclude": [], "broker":12345, "address":"127.0.0.1", "dev":true }' > /etc/iofog-connector/iofog-connector.conf

CMD [ "sh", "/start.sh" ]