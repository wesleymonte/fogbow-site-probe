FROM    tma-monitor/libs:0.1

ENV     probes      /atmosphere/tma/probe

#       Adding Monitor Client
WORKDIR ${probes}/probe-fogbow

#       Prepare by downloading dependencies
COPY    pom.xml     ${probes}/probe-fogbow/pom.xml

#       Copying probe-fogbow.conf
COPY    pom.xml     ${probes}/probe-fogbow/probe-fogbow.conf

#       Adding source, compile and package into a fat jar
COPY    src ${probes}/probe-fogbow/src
RUN     ["mvn", "install"]

RUN     ["cp", "-r", "bin", "/atmosphere/tma/probe/bin"]

CMD     ["java", "-jar", "/atmosphere/tma/probe/bin/probe-fogbow-0.1.jar", "/atmosphere/tma/probe/probe-fogbow/probe-fogbow.conf"]
