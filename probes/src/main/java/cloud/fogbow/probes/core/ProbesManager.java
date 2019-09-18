package cloud.fogbow.probes.core;

import cloud.fogbow.probes.core.controllers.DockerProbesController;
import cloud.fogbow.probes.core.controllers.FogbowProbesController;
import cloud.fogbow.probes.core.services.DataProviderService;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProbesManager {

    private static final Logger LOGGER = LogManager.getLogger(ProbesManager.class);
    private static ProbesManager ourInstance = new ProbesManager();
    private FogbowProbesController fogbowProbesController;
    private DockerProbesController dockerProbesController;
    private Properties properties;

    public static ProbesManager getInstance() {
        return ourInstance;
    }

    public void init(DataProviderService dataProviderService) {
        this.fogbowProbesController = new FogbowProbesController(properties);
        this.fogbowProbesController.init(dataProviderService);
        this.dockerProbesController = new DockerProbesController(properties);
    }

    public void start() {
        LOGGER.info("Starting Probes...");
        fogbowProbesController.startAll();
        dockerProbesController.startAll();
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
