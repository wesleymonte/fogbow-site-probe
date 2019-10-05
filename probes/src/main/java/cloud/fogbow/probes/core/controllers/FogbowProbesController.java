package cloud.fogbow.probes.core.controllers;

import cloud.fogbow.probes.core.Constants;
import cloud.fogbow.probes.core.PropertiesHolder;
import cloud.fogbow.probes.core.controllers.threadfactory.DefaultThreadFactory;
import cloud.fogbow.probes.core.probes.fogbow.FogbowProbe;
import cloud.fogbow.probes.core.probes.fogbow.FogbowResourceAvailabilityProbe;
import cloud.fogbow.probes.core.services.DataProviderService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FogbowProbesController {

    private static final Logger LOGGER = LogManager.getLogger(FogbowProbesController.class);
    private static final String THREAD_NAME_PREFIX = "Fogbow-Probe-";
    private static final int POOL_SIZE = 4;

    private boolean isStarted;
    private FogbowProbe resourceAvailabilityProbe;
    private FogbowProbe serviceLatencyProbe;
    private FogbowProbe serviceSuccessRateProbe;
    private FogbowProbe serviceReachabilityProbe;
    private ScheduledExecutorService scheduled;

    public FogbowProbesController() {
        this.scheduled = new ScheduledThreadPoolExecutor(POOL_SIZE,
            new DefaultThreadFactory(THREAD_NAME_PREFIX));
    }

    public void init(DataProviderService dataProviderService) {
//        LOGGER.debug("Init the Fogbow Probes Controller: FTA ADDRESS [" + ftaAddress + "]");
        FogbowResourceAvailabilityProbe fogbowResourceAvailabilityProbe = new FogbowResourceAvailabilityProbe(dataProviderService);
        this.resourceAvailabilityProbe = new FogbowProbe(fogbowResourceAvailabilityProbe, dataProviderService);

        FogbowResourceAvailabilityProbe fogbowServiceLatencyProbe = new FogbowResourceAvailabilityProbe(dataProviderService);
        this.serviceLatencyProbe = new FogbowProbe(fogbowServiceLatencyProbe, dataProviderService);

        FogbowResourceAvailabilityProbe fogbowServiceSuccessRateProbe = new FogbowResourceAvailabilityProbe(dataProviderService);
        this.serviceSuccessRateProbe = new FogbowProbe(fogbowServiceSuccessRateProbe, dataProviderService);

        FogbowResourceAvailabilityProbe fogbowServiceReachabilityProbe = new FogbowResourceAvailabilityProbe(dataProviderService);
        this.serviceReachabilityProbe = new FogbowProbe(fogbowServiceReachabilityProbe, dataProviderService);
    }

    public void startAll() {
        LOGGER.info("Starting Fogbow Probes Threads...");
        if (!isStarted) {
            submitTasks();
            isStarted = true;
        }
    }

    private void submitTasks() {
        long delay = Long.parseLong(PropertiesHolder.getInstance().getProperty(Constants.DELAY));
        long initialDelay = 5000;
        LOGGER.debug(
            "Scheduling Fogbow Container Probes: INITIAL_DELAY [" + initialDelay + "]; DELAY ["
                + delay + "]");
        scheduled.scheduleWithFixedDelay(resourceAvailabilityProbe, initialDelay, delay,
            TimeUnit.MILLISECONDS);
        scheduled.scheduleWithFixedDelay(serviceLatencyProbe, initialDelay, delay,
            TimeUnit.MILLISECONDS);
        scheduled.scheduleWithFixedDelay(serviceSuccessRateProbe, initialDelay, delay,
            TimeUnit.MILLISECONDS);
        scheduled.scheduleWithFixedDelay(serviceReachabilityProbe, initialDelay, delay,
            TimeUnit.MILLISECONDS);
    }

}
