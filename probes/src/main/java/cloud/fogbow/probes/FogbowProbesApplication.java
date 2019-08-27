package cloud.fogbow.probes;

import static cloud.fogbow.probes.core.utils.PropertiesUtil.loadProperties;

import cloud.fogbow.probes.core.Constants;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication()
public class FogbowProbesApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(FogbowProbesApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(FogbowProbesApplication.class, args);
    }

    @Bean
    public Properties properties() {
        Properties properties = null;
        try {
            String path = Thread.currentThread().getContextClassLoader().getResource("").getPath()
                + "private/";
            properties = loadProperties(path + Constants.CONF_FILE);
        } catch (Exception e) {
            LOGGER.error("Error while load properties.", e);
            System.exit(1);
        }
        return properties;
    }
}
