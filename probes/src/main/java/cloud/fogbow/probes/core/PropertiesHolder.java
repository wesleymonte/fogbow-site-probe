package cloud.fogbow.probes.core;

import cloud.fogbow.probes.core.utils.AppUtil;
import java.util.Properties;

public class PropertiesHolder {

    public static final String CONF_FILE_PROPERTY = "conf_file";

    private static PropertiesHolder instance;
    private Properties properties;

    private PropertiesHolder() {
        this.properties = AppUtil.readProperties();
    }

    public static synchronized PropertiesHolder getInstance() {
        if (instance == null) {
            instance = new PropertiesHolder();
        }
        return instance;
    }

    public String getHostLabelProperty(){
        return this.getProperty(Constants.TARGET_LABEL);
    }

    public String getHostAddressProperty(){
        return this.getProperty(Constants.PROBE_TARGET);
    }

    public String getFtaAddressProperty(){
        return this.getProperty(Constants.FTA_ADDRESS);
    }

    public String getTargetDockerPortProperty(){
        return this.getProperty(Constants.TARGET_DOCKER_PORT);
    }

    public String getProperty(String propertyName) {
        return properties.getProperty(propertyName);
    }

    public String getProperty(String propertyName, String defaultPropertyValue) {
        String propertyValue = this.properties.getProperty(propertyName, defaultPropertyValue);
        if (propertyValue.trim().isEmpty()) {
            propertyValue = defaultPropertyValue;
        }
        return propertyValue;
    }

    public Properties getProperties() {
        return this.properties;
    }
}
