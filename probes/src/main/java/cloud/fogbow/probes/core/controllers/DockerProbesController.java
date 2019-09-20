package cloud.fogbow.probes.core.controllers;

import cloud.fogbow.probes.core.Constants;
import cloud.fogbow.probes.core.probes.docker.DockerContainerProbe;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DockerProbesController {


    private static final Logger LOGGER = LogManager.getLogger(FogbowProbesController.class);
    private static final String THREAD_NAME_PREFIX = "Docker-Probe-";
    private DockerContainerProbe dockerContainerProbe;
    private List<Thread> pool;
    private boolean isStarted = false;
    private Properties properties;
    private ScheduledExecutorService scheduled = new ScheduledThreadPoolExecutor(1,
        new DefaultThreadFactory(THREAD_NAME_PREFIX));

    public DockerProbesController(Properties properties) {
        this.properties = properties;
    }

    public void init() {
        Integer sleepTime = Integer.valueOf(properties.getProperty(Constants.SLEEP_TIME));
        String ftaAddress = properties.getProperty(Constants.FTA_ADDRESS);
        LOGGER.info("Defining Docker Probes:\n\tSleep time [" + sleepTime
            + "]\n\tFogbow Telemetry Aggregator Address [" + ftaAddress + "]");
        dockerContainerProbe = new DockerContainerProbe(ftaAddress);
    }

    private void submitTasks() {
        long delay = Long.parseLong(properties.getProperty(Constants.SLEEP_TIME));
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
