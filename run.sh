#!/bin/bash

## general setup
DIR_PATH="`dirname \"$0\"`"              # relative
DIR_PATH="`( cd \"$DIR_PATH\" && pwd )`"  # absolutized and normalized
if [ -z "$DIR_PATH" ] ; then
  # error; for some reason, the path is not accessible
  # to the script (e.g. permissions re-evaled after suid)
  exit 1  # fail
fi

# docker setup
# sudo apt-get remove docker docker-engine docker.io containerd runc
# sudo apt-get update
# sudo apt-get install \
#     apt-transport-https \
#     ca-certificates \
#     curl \
#     gnupg-agent \
#     software-properties-common
# curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -

# sudo apt-key fingerprint 0EBFCD88

# sudo add-apt-repository \
#    "deb [arch=amd64] https://download.docker.com/linux/ubuntu \
#    $(lsb_release -cs) \
#    stable"

# sudo apt-get update

# sudo apt-get install docker-ce docker-ce-cli containerd.io

# container setup

image_tag=${1-latest}
image=eubraatmosphere/fogbow-probes:$image_tag
conf_file=$DIR_PATH/probe-fogbow.conf
ip=`awk -F ' *= *' '$1=="monitor_ip"{print $2}' $conf_file`/monitor
endpoint_attr=DEFAULT_ENDPOINT

sudo docker pull $image
container_id=`sudo docker run -idt $image`
sudo docker cp $conf_file $container_id:/app/probes/src/main/resources/private
sudo docker cp $DIR_PATH/cert.pem $container_id:/app/
sudo docker cp $DIR_PATH/cert.pem $container_id:/app/java-client-lib
sudo docker exec -it $container_id sed -i "s,$endpoint_attr.*,$endpoint_attr=$ip;," /app/java-client-lib/src/main/java/eu/atmosphere/tmaf/monitor/client/MonitorClient.java
sudo docker exec -it $container_id sed -i "s,spring.datasource.url.*,spring.datasource.url=jdbc:postgresql://10.11.4.173:5432/ras," /app/probes/src/main/resources/application.properties
sudo docker exec -it $container_id sed -i "s,spring.datasource.username.*,spring.datasource.username=fogbow," /app/probes/src/main/resources/application.properties
sudo docker exec -it $container_id sed -i "s,spring.datasource.password.*,spring.datasource.password=jooBahx6ai," /app/probes/src/main/resources/application.properties
sudo docker exec -it $container_id chmod 777 probes
echo "LAAAAST LINEEEEEEEEEE"
sudo docker exec -it $container_id /bin/bash -c "cd /app/probes && mvn spring-boot:run -X > log.out 2> log.err" &

