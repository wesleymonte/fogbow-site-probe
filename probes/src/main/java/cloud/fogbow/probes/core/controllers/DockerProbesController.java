package cloud.fogbow.probes.core.controllers;

import cloud.fogbow.probes.core.Constants;
import cloud.fogbow.probes.core.probes.docker.DockerContainerProbe;
import java.util.Properties;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DockerProbesController {

    private static final Logger LOGGER = LogManager.getLogger(FogbowProbesController.class);
    private static final String THREAD_NAME_PREFIX = "Docker-Probe-";
    private static final int POOL_SIZE = 1;

    private boolean isStarted;
    private Properties properties;
    private DockerContainerProbe dockerContainerProbe;
    private ScheduledExecutorService scheduled;

    public DockerProbesController(Properties properties) {
        this.properties = properties;
        this.scheduled = new ScheduledThreadPoolExecutor(POOL_SIZE,
            new DefaultThreadFactory(THREAD_NAME_PREFIX));
    }

    public void init() {
        String ftaAddress = properties.getProperty(Constants.FTA_ADDRESS);
        LOGGER.debug("Init the Docker Probes Controller: FTA ADDRESS [" + ftaAddress + "]");
        this.dockerContainerProbe = new DockerContainerProbe(ftaAddress);
    }

    private void submitTasks() {
        long delay = Long.parseLong(properties.getProperty(Constants.DELAY));
        long initialDelay = 5;
        scheduled
            .scheduleWithFixedDelay(dockerContainerProbe, initialDelay, delay, TimeUnit.SECONDS);
    }

    public void startAll() {
        LOGGER.info("Starting Docker Probes Threads...");
        if (!isStarted) {
            submitTasks();
            isStarted = true;
        }
    }

}
