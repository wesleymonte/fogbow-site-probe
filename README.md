# Fogbow Site Probe
Fogbow Site Probe is a probe of the Fogbow project created by Laboratório de Sistemas Discribuidos(LSD) of the Universidade de Campina Grande(UFCG).

This is a probe that pushes metrics from a fogbow site to a TMA instance.

### Configuration 
Rename the file "probe-fogbow.conf.example" to "probe-fogbow.conf" and configure it.

```bash
# database
database_url=
user_database=
password_database=
database_driver= 
		
# scheduler
scheduler_period=
		
# monitor ATM
monitor_url=
probe_id=
resource_id=
probe_password=
```
**database_url**(Required):: RAS database url.
**user_database**(Required):: RAS database user.
**password_database**(Required):: RAS database password.
**database_driver**(Required):: RAS database drive. Type of database.

**scheduler_period**: Period of send messages to the TMA monitor.

**monitor_url**(Required):: TMA Monitor url
**probe_id**(Required):: Probe id in the TMA
**resource_id**(Required): Resource id in the TMA
**probe_password**: Probe password

## Deploy

### Docker
#### Pre requirements
Create docker image of this project. [TMA-monitor-libraries](https://github.com/eubr-atmosphere/tma-framework-m/tree/master/development/libraries)

Note: Use the certificate generated and configurated in the TMA Monitor deploy. Change this file "[cert.pem](https://github.com/eubr-atmosphere/tma-framework-m/blob/master/development/libraries/cert.pem)".

```bash
bash build.sh
```

#### Configure and create Docker image
In the actual project.

1. Configure the probe-fogbow.conf.

2. Create Docker image
```bash
bash build.sh
```

#### Run
```bash
sudo docker run -it fogbow/probe-fogbow:0.1 /bin/bash
```

### Manually

#### Pre requirements 
Install java-jdk-8.

#### Import the monitor certificate
1. Import certificates to the JAVA
Note: Use the certificate generated and configurated in the TMA Monitor deploy.
```bash
keytool -import -noprompt -trustcacerts -alias <AliasName> -file   <certificate> -keystore <KeystoreFile> -storepass <Password>
```

#### Configure
1. Configure the probe-fogbow.conf.

#### Run
```bash
java -jar {your_path}/bin/probe-fogbow-0.1.jar {your_path}/probe-fogbow.conf
```
