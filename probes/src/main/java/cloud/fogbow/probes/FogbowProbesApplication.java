package cloud.fogbow.probes;

import cloud.fogbow.probes.core.PropertiesHolder;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication()
public class FogbowProbesApplication {

    private static final Logger LOGGER = LogManager.getLogger(FogbowProbesApplication.class);

    public static void main(String[] args) {
        loadArguments(args);
        SpringApplication.run(FogbowProbesApplication.class, args);
    }

    private static void loadArguments(String[] args) {
        Options options = new Options();

        String opt = "c";
        String longOpt = PropertiesHolder.CONF_FILE_PROPERTY;
        String description = "Configuration file path";
        Option confFilePath = new Option(opt, longOpt, true, description);
        confFilePath.setRequired(false);
        options.addOption(confFilePath);

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
            if (cmd.hasOption(longOpt)) {
                String inputFilePath = cmd.getOptionValue(longOpt);
                System.setProperty(PropertiesHolder.CONF_FILE_PROPERTY, inputFilePath);
            }
        } catch (ParseException e) {
            LOGGER.error("Error while loading command line arguments: " + e.getMessage(), e);
            System.exit(1);
        }
    }
}
