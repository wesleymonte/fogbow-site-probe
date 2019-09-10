FROM openjdk:8

#Define working directory
WORKDIR /service

COPY ./target/probe-fogbow-0.1.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]