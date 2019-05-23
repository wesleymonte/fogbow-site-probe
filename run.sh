#!/bin/bash

DIR_PATH="`dirname \"$0\"`"              # relative
DIR_PATH="`( cd \"$DIR_PATH\" && pwd )`"  # absolutized and normalized
if [ -z "$DIR_PATH" ] ; then
  # error; for some reason, the path is not accessible
  # to the script (e.g. permissions re-evaled after suid)
  exit 1  # fail
fi

## install docker
# sudo apt-get remove docker docker-engine docker.io containerd runc
# sudo apt-get update
# sudo apt-get install \
#     apt-transport-https \
#     ca-certificates \
#     curl \
#     gnupg-agent \
#     software-properties-common
# curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -

# expected_key=pub   rsa4096 2017-02-22 [SCEA]
#       9DC8 5822 9FC7 DD38 854A  E2D8 8D81 803C 0EBF CD88
# uid           [ unknown] Docker Release (CE deb) <docker@docker.com>
# sub   rsa4096 2017-02-22 [S]

# key=`sudo apt-key fingerprint 0EBFCD88`

# if [ "$expected_key" != "$key" ] then;
# 	echo "error when adding the key"
# fi

# sudo add-apt-repository \
#    "deb [arch=amd64] https://download.docker.com/linux/ubuntu \
#    $(lsb_release -cs) \
#    stable"

# sudo apt-get update

# sudo apt-get install docker-ce docker-ce-cli containerd.io

## copy the conf file and lauch the container
image_tag=${1-latest}
image=eubraatmosphere/fogbow-probes:$image_tag
conf_file=$DIR_PATH/probe-fogbow.conf
ip=`awk -F ' *= *' '$1=="monitor_ip"{print $2}' $conf_file`
endpoint_attr=DEFAULT_ENDPOINT
container_id=`sudo docker run -idt $image`

sudo docker cp $conf_file $container_id:/app/probes/src/main/resources/private
sudo docker cp $DIR_PATH/get-certificate.sh $container_id:/app/
echo "ok"
sudo docker exec -it $container_id sed -i 's/$endpoint_attr.*/$endpoint_attr=$ip/' /app/java-client-lib/src/main/java/eu/atmosphere/tmaf/monitor/client/MonitorClient.java
echo "ok"
# sudo docker exec -it $container_id chmod 601 /app/get-certificate.sh
# echo "ok"
# sudo docker exec -it $container_id /app/get-certificate.sh
# echo "ok"
sudo docker exec -it $container_id chmod 777 probes
echo ok
sudo docker exec -it $container_id cd /app/probes 
sudo docker exec -it $container_id ls -la
sudo docker exec -it $container_id mvn spring-boot:run
echo "ok"


