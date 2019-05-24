FROM    maven:3.5.3-jdk-8-alpine

ENV     libs /atmosphere/tma/libraries


#Add Monitor Client
WORKDIR ${libs}/monitor-client
COPY    cert.pem ${libs}/monitor-client
WORKDIR /usr/lib/jvm/java-1.8-openjdk/jre/bin

RUN     ["./keytool", "-import", "-trustcacerts", "-keystore", "/usr/lib/jvm/java-1.8-openjdk/jre/lib/security/cacerts", "-storepass", "changeit", "-noprompt", "-alias", "monitor", "-file", "/atmosphere/tma/libraries/monitor-client/cert.pem"]

WORKDIR ${libs}/monitor-client

# Prepare by downloading dependencies
ADD     pom.xml  ${libs}/monitor-client/pom.xml
RUN     ["mvn", "dependency:resolve"]
RUN     ["mvn", "verify"]

# Add source, compile and package into a fat jar
ADD     src      ${libs}/monitor-client/src
RUN     ["mvn", "install"]




