#/bin/bash

readonly TAG=latest
readonly IMAGE=wesleymonte/probes

echo "Run this script outside the docker folder."
echo "(e.g.) sudo bash ~/probes/docker/deploy.sh"
printf "For configuration, edit the files:\n\t./src/main/resources/private/probe-fogbow.conf\n\t./src/main/resources/application.properties\n"
while true; do
    read -p "Is ok?" yn
    case $yn in
        [Yy]* ) 
        mvn package;
        docker build --no-cache -t $IMAGE:$TAG -f docker/Dockerfile .; 
        docker run -itd --name probes $IMAGE:$TAG
        break;;
        [Nn]* ) exit;;
        * ) echo "Please answer yes or no.";;
    esac
done