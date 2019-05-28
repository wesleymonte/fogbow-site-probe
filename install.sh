#!/bin/bash
USER=ubuntu

ssh_key_file=`awk -F ' *= *' '$1=="ssh-private-key"{print $2}' ./probe-fogbow.conf`
ip=`awk -F ' *= *' '$1=="host-ip"{print $2}' ./probe-fogbow.conf`

ssh -i $ssh_key_file $USER@$ip << EOF 
	cd
	git clone https://github.com/eubr-atmosphere/fogbow-site-probe.git
EOF

deploy_file=setup-conf-files.yml
(cd ansible && ansible-playbook -v $deploy_file)

img_version=`awk -F ' *= *' '$1=="img-version"{print $2}' ./probe-fogbow.conf`

ssh -i $ssh_key_file $USER@$ip << EOF 
	cd ~/fogbow-site-probe
	./run.sh $img_version
EOF

exit 0
