### Configuration
This part boils down to filling a file called probe-fogbow.conf and to include a file called cert.pem that wrapps the ssl certificate.
Both must be in the root path.

The probe-fogbow.conf must be filled by following the probe-fogbow-template.conf.

### Deploy

Once the configuration is done, just call ./install.sh to deploy the application in the host, as set in the conf file.