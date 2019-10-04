package cloud.fogbow.probes.core;

import cloud.fogbow.probes.core.controllers.DockerProbesController;
import cloud.fogbow.probes.core.controllers.FogbowProbesController;
import cloud.fogbow.probes.core.services.DataProviderService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProbesManager {

    private static final Logger LOGGER = LogManager.getLogger(ProbesManager.class);
    private static ProbesManager ourInstance = new ProbesManager();
    private FogbowProbesController fogbowProbesController;
    private DockerProbesController dockerProbesController;

    public static ProbesManager getInstance() {
        return ourInstance;
    }

    public void init(DataProviderService dataProviderService) {
        this.fogbowProbesController = new FogbowProbesController();
        this.fogbowProbesController.init(dataProviderService);
        this.dockerProbesController = new DockerProbesController();
        this.dockerProbesController.init();
    }

    public void start() {
        LOGGER.info("Starting Probes...");
        fogbowProbesController.startAll();
        dockerProbesController.startAll();
    }
}
