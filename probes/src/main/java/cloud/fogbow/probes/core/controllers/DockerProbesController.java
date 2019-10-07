package cloud.fogbow.probes.core.controllers;

import cloud.fogbow.probes.core.Constants;
import cloud.fogbow.probes.core.PropertiesHolder;
import cloud.fogbow.probes.core.controllers.threadfactory.DefaultThreadFactory;
import cloud.fogbow.probes.core.probes.docker.DockerContainerMetricCollector;
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
    private DockerContainerMetricCollector dockerContainerMetricCollector;
    private ScheduledExecutorService scheduled;

    public DockerProbesController() {
        this.scheduled = new ScheduledThreadPoolExecutor(POOL_SIZE,
            new DefaultThreadFactory(THREAD_NAME_PREFIX));
    }

    public void init() {
//        LOGGER.debug("Init the Docker Probes Controller: FTA ADDRESS [" + ftaAddress + "]");
        this.dockerContainerMetricCollector = new DockerContainerMetricCollector();
    }

    public void startAll() {
        LOGGER.info("Starting Docker Probes Threads...");
        if (!isStarted) {
            submitTasks();
            isStarted = true;
        }
    }

    private void submitTasks() {
        long delay = Long.parseLong(PropertiesHolder.getInstance().getProperty(Constants.DELAY));
        long initialDelay = 5000;
        LOGGER.debug(
            "Scheduling Docker Container Probes: INITIAL_DELAY [" + initialDelay + "]; DELAY ["
                + delay + "]");
        scheduled.scheduleWithFixedDelay(dockerContainerMetricCollector, initialDelay, delay,
            TimeUnit.MILLISECONDS);
    }

}
