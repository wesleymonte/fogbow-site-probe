package cloud.fogbow.probes.core.controllers;

import cloud.fogbow.probes.core.Constants;
import cloud.fogbow.probes.core.probes.docker.DockerContainerProbe;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DockerProbesController {

    private static final Logger LOGGER = LogManager.getLogger(FogbowProbesController.class);
    private DockerContainerProbe dockerContainerProbe;
    private List<Thread> pool;
    private boolean isStarted = false;
    private Properties properties;

    public DockerProbesController(Properties properties) {
        this.properties = properties;
    }

    public void init() {
        Integer sleepTime = Integer.valueOf(properties.getProperty(Constants.SLEEP_TIME));
        String ftaAddress = properties.getProperty(Constants.FTA_ADDRESS);
        LOGGER.info("Defining Docker Probes:\n\tSleep time [" + sleepTime
            + "]\n\tFogbow Telemetry Aggregator Address [" + ftaAddress + "]");
        dockerContainerProbe = new DockerContainerProbe(sleepTime, ftaAddress);
    }

    public void startAll() {
        LOGGER.info("Starting Docker Probes Threads...");
        if (!isStarted) {
            createThreads();
            for (Thread t : pool) {
                t.start();
            }
            isStarted = true;
        }
    }

    private void createThreads() {
        Thread firstProbe = new Thread(dockerContainerProbe, DockerContainerProbe.THREAD_NAME);
        pool = new ArrayList<>(Arrays.asList(firstProbe, firstProbe));
    }

}
