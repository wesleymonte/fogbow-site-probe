FROM openjdk:8

WORKDIR /app

#       Prepare by downloading dependencies
COPY    . /app
	
RUN	\
	apt-get update -y && \
	apt-get upgrade -y && \
	apt-get install -y maven curl git lsof vim nano 

RUN	apt-get install openjfx -y

RUN     cd /app/probes &&\ 
	mvn clean install

EXPOSE 80

CMD	(cd /app/probes && mvn spring-boot:run)
