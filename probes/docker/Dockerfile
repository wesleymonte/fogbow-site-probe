FROM openjdk:8

#Define working directory
WORKDIR /service

COPY ./target/probe-fogbow-0.1.jar /service

ENTRYPOINT ["java", "-jar", "/service/probe-fogbow-0.1.jar"]