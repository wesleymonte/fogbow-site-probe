FROM openjdk:8

ARG PROBE_BRANCH

#install
RUN \
    apt-get update -y && \
    apt-get upgrade -y && \
    apt-get install -y maven curl git lsof vim nano

#Set environment variables
ENV HOME /root

#Define working directory
WORKDIR /root

RUN \
    git clone -b $PROBE_BRANCH https://github.com/wesleymonte/fogbow-site-probe.git && \
    (cd fogbow-site-probe/java-client-lib && git branch && mvn clean install -DskipTests)



