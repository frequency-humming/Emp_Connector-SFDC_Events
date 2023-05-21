FROM amazoncorretto:11

RUN yum install -y curl

VOLUME /tmp
COPY target/SFDC_Events-0.0.4-SNAPSHOT-phat.jar app.jar

COPY ./entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

ENTRYPOINT ["/entrypoint.sh"]