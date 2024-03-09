FROM --platform=linux/amd64 amazoncorretto:11

RUN yum install -y curl

VOLUME /tmp
COPY target/SFDC_Events-0.0.5-SNAPSHOT-phat.jar app.jar

COPY ./entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

ENTRYPOINT ["/entrypoint.sh"]