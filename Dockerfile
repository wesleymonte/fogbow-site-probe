# 		TODO change this
FROM    tma-monitor/libs:0.1

ENV     probes      /atmosphere/tma/probe

#       Adding Monitor Client
WORKDIR ${probes}/probe-fogbow

#       Prepare by downloading dependencies
COPY    pom.xml     ${probes}/probe-fogbow/pom.xml

#       Adding source, compile and package into a fat jar
COPY    src ${probes}/probe-fogbow/src
RUN     ["mvn", "install"]

RUN     ["cp", "-r", "bin", "/atmosphere/tma/probe/bin"]

# 		TODO pass "properties file" with parameters for the JAR  
CMD     ["java", "-jar", "/atmosphere/tma/probe/bin/probe-fogbow-0.1.jar"]


