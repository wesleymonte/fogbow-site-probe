FROM    maven:3.5.3-jdk-8

WORKDIR /app

#       Prepare by downloading dependencies
COPY    . /app

RUN	\
	apt-get update -y && \
	apt-get upgrade -y && \
	apt-get install -y maven curl git lsof vim nano 

RUN	apt-get install openjfx -y

EXPOSE 80

CMD ('/bin/sh')
