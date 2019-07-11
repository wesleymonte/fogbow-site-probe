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
install-docker() {
    echo "--> Installing docker"
    apt update

    apt-get install -y \
        apt-transport-https \
        ca-certificates \
        curl \
        software-properties-common
    
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -

    apt-key fingerprint 0EBFCD88

    add-apt-repository \
    "deb [arch=amd64] https://download.docker.com/linux/ubuntu \
    $(lsb_release -cs) \
    stable"

    apt-get update

    apt-get install -y docker-ce
}

CHECK_DOCKER_INSTALLATION=$(dpkg -l | grep -c docker-ce)

if ! [ $CHECK_DOCKER_INSTALLATION -ne 0 ]; then
    install-docker
else 
    echo "--> Docker its already installed"
fi

image_tag=${1-latest}
image=eubraatmosphere/fogbow-probes:$image_tag
conf_file=$DIR_PATH/probe-fogbow.conf
ip=`awk -F ' *= *' '$1=="monitor_ip"{print $2}' $conf_file`/monitor
endpoint_attr="DEFAULT_ENDPOINT ="
ras_db_url=`awk -F ' *= *' '$1=="ras_db_url"{print $2}' $conf_file`/ras
db_username=`awk -F ' *= *' '$1=="db_username"{print $2}' $conf_file`
db_password=`awk -F ' *= *' '$1=="db_password"{print $2}' $conf_file`

sudo docker pull $image
container_id=`sudo docker run -idt $image`
sudo docker cp $DIR_PATH/java-client-lib $container_id:/app/
sudo docker cp $DIR_PATH/probes $container_id:/app/
sudo docker cp $conf_file $container_id:/app/probes/src/main/resources/private
sudo docker cp $DIR_PATH/cert.pem $container_id:/app/
sudo docker cp $DIR_PATH/cert.pem $container_id:/app/java-client-lib
sudo docker exec $container_id /bin/bash -c "sed -i 's,$endpoint_attr.*,$endpoint_attr\"$ip\";,' /app/java-client-lib/src/main/java/eu/atmosphere/tmaf/monitor/client/MonitorClient.java"
sudo docker exec $container_id /bin/bash -c "sed -i 's,spring.datasource.url.*,spring.datasource.url=jdbc:postgresql://$ras_db_url,' /app/probes/src/main/resources/application.properties"
sudo docker exec $container_id /bin/bash -c "sed -i 's,spring.datasource.username.*,spring.datasource.username=$db_username,' /app/probes/src/main/resources/application.properties"
sudo docker exec $container_id /bin/bash -c "sed -i 's,spring.datasource.password.*,spring.datasource.password=$db_password,' /app/probes/src/main/resources/application.properties"
sudo docker exec $container_id /bin/bash -c "rm -rf /usr/share/maven/boot/plexus-classworlds-2.5.2.jar"
sudo docker exec $container_id /bin/bash -c "keytool -import -trustcacerts -keystore /usr/lib/jvm/java-1.8.0-openjdk-amd64/jre/lib/security/cacerts -storepass changeit -noprompt -alias monitor -file cert.pem"
sudo docker exec -d $container_id /bin/bash -c "cd /app/java-client-lib && mvn clean install && cd /app/probes && mvn clean install && mvn spring-boot:run -X > log.out 2> log.err" &

