package cloud.fogbow.probes;

import static cloud.fogbow.probes.core.utils.PropertiesUtil.loadProperties;

import cloud.fogbow.probes.core.Constants;
import java.util.Objects;
import java.util.Properties;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication()
public class FogbowProbesApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(FogbowProbesApplication.class);

    public static void main(String[] args) {
        loadArguments(args);
        SpringApplication.run(FogbowProbesApplication.class, args);
    }

    @Bean
    public Properties properties() {
        Properties properties = null;
        String confFilePath = System.getProperty("conf_file");

        try {
            if (Objects.isNull(confFilePath)) {
                confFilePath =
                    Thread.currentThread().getContextClassLoader().getResource("").getPath()
                        + "private/";
                LOGGER.info("Configuration file found in default path " + confFilePath + ".");
                properties = loadProperties(confFilePath + Constants.CONF_FILE);
            } else {
                LOGGER.info("Configuration file found in path " + confFilePath + ".");
                properties = loadProperties(confFilePath);
            }
        } catch (Exception e) {
            LOGGER.error("Error while load properties.", e);
            System.exit(1);
        }
        return properties;
    }

    private static void loadArguments(String[] args){
        Options options = new Options();

        String opt = "c";
        String longOpt = "conf_file";
        String description = "Configuration file path";
        Option confFilePath = new Option(opt, longOpt, true, description);
        confFilePath.setRequired(false);
        options.addOption(confFilePath);

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
            String inputFilePath = cmd.getOptionValue(longOpt);
            System.setProperty(longOpt, inputFilePath);
        } catch (ParseException e) {
            LOGGER.error("Error while loading command line arguments: " + e.getMessage(), e);
            System.exit(1);
        }
    }
}
