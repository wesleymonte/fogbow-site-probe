FROM openjdk:8

#Define working directory
WORKDIR /service

COPY ./target/probe-fogbow-0.1.jar /service
COPY ./src/main/resources/private/probe-fogbow.conf /service
COPY ./src/main/resources/application.properties /service

ENTRYPOINT ["java", "-jar", "/service/probe-fogbow-0.1.jar", "--conf_file", "/service/probe-fogbow.conf"]