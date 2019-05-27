#!/bin/bash

ssh_key_file=ip=awk -F ' *= *' '$1=="ssh-private-key"{print $2}' ./probe-fogbow.conf
ip=awk -F ' *= *' '$1=="host-ip"{print $2}' ./probe-fogbow.conf
ssh -i ssh_key_file $USER@ip

fogbow-probe-repo=https://github.com/eubr-atmosphere/fogbow-site-probe.git
(cd ~/ && git clone $fogbow-probe-repo && exit 0)

deploy_file=deploy.yml
(cd ansible && ansible-playbook $deploy_file)

img_version=awk -F ' *= *' '$1=="img-version"{print $2}' ./probe-fogbow.conf
ssh -i ssh_key_file $USER@ip
(cd ~/fogbow-site-probe && ./run.sh $img_version)
exit 0