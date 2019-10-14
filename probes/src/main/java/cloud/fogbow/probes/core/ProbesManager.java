package cloud.fogbow.probes.core;

import cloud.fogbow.probes.core.controllers.DefaultProbesController;
import cloud.fogbow.probes.core.probes.ProbeCreator;
import cloud.fogbow.probes.core.probes.docker.creators.DockerProbeCreator;
import cloud.fogbow.probes.core.probes.fogbow.creator.FogbowProbeCreator;
import cloud.fogbow.probes.provider.DataProviderService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProbesManager {

    private static final Logger LOGGER = LogManager.getLogger(ProbesManager.class);
    private static ProbesManager ourInstance = new ProbesManager();
    private DefaultProbesController defaultProbesController;

    public static ProbesManager getInstance() {
        return ourInstance;
    }

    public ProbesManager() {
        this.defaultProbesController = new DefaultProbesController();
    }

    public void startFogbowProbes(DataProviderService dataProviderService){
        LOGGER.info("Starting Fogbow Probes...");
        ProbeCreator fogbowProbeCreator = new FogbowProbeCreator(dataProviderService);
        defaultProbesController.setProbeCreator(fogbowProbeCreator);
        defaultProbesController.submitProbes();
    }

    public void startDockerProbes(){
        LOGGER.info("Starting Docker Probes...");
        ProbeCreator dockerProbeCreator = new DockerProbeCreator();
        defaultProbesController.setProbeCreator(dockerProbeCreator);
        defaultProbesController.submitProbes();
    }
}
