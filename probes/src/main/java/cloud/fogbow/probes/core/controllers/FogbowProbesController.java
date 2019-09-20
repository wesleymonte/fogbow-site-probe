package cloud.fogbow.probes.core.controllers;

import cloud.fogbow.probes.core.Constants;
import cloud.fogbow.probes.core.probes.fogbow.FogbowResourceAvailabilityProbe;
import cloud.fogbow.probes.core.probes.fogbow.FogbowServiceLatencyProbe;
import cloud.fogbow.probes.core.probes.fogbow.FogbowServiceReachabilityProbe;
import cloud.fogbow.probes.core.probes.fogbow.FogbowServiceSuccessRateProbe;
import cloud.fogbow.probes.core.services.DataProviderService;
import java.util.Properties;
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
    private Properties properties;
    private FogbowResourceAvailabilityProbe resourceAvailabilityProbe;
    private FogbowServiceLatencyProbe serviceLatencyProbe;
    private FogbowServiceSuccessRateProbe serviceSuccessRateProbe;
    private FogbowServiceReachabilityProbe serviceReachabilityProbe;
    private ScheduledExecutorService scheduled;

    public FogbowProbesController(Properties properties) {
        this.properties = properties;
        this.scheduled = new ScheduledThreadPoolExecutor(POOL_SIZE,
            new DefaultThreadFactory(THREAD_NAME_PREFIX));
    }

    public void init(DataProviderService dataProviderService) {
        String ftaAddress = properties.getProperty(Constants.FTA_ADDRESS);
        LOGGER.info(
            "Defining Fogbow Probes:\n\tFogbow Telemetry Aggregator Address [" + ftaAddress + "]");
        this.resourceAvailabilityProbe = new FogbowResourceAvailabilityProbe(ftaAddress);
        this.serviceLatencyProbe = new FogbowServiceLatencyProbe(ftaAddress);
        this.serviceSuccessRateProbe = new FogbowServiceSuccessRateProbe(ftaAddress);
        this.serviceReachabilityProbe = new FogbowServiceReachabilityProbe(ftaAddress,
            properties.getProperty(Constants.AS_ENDPOINT),
            properties.getProperty(Constants.RAS_ENDPOINT),
            properties.getProperty(Constants.FNS_ENDPOINT),
            properties.getProperty(Constants.MS_ENDPOINT));
        this.setProviderService(dataProviderService);
    }

    private void submitTasks() {
        long delay = Long.parseLong(properties.getProperty(Constants.SLEEP_TIME));
        long initialDelay = 5;
        scheduled.scheduleWithFixedDelay(resourceAvailabilityProbe, initialDelay, delay,
            TimeUnit.SECONDS);
        scheduled
            .scheduleWithFixedDelay(serviceLatencyProbe, initialDelay, delay, TimeUnit.SECONDS);
        scheduled
            .scheduleWithFixedDelay(serviceSuccessRateProbe, initialDelay, delay, TimeUnit.SECONDS);
        scheduled.scheduleWithFixedDelay(serviceReachabilityProbe, initialDelay, delay,
            TimeUnit.SECONDS);
    }

    public void startAll() {
        LOGGER.info("Starting Fogbow Probes Threads...");
        if (!isStarted) {
            submitTasks();
            isStarted = true;
        }
    }

    private void setProviderService(DataProviderService dataProviderService) {
        resourceAvailabilityProbe.setProviderService(dataProviderService);
        serviceLatencyProbe.setProviderService(dataProviderService);
        serviceSuccessRateProbe.setProviderService(dataProviderService);
        serviceReachabilityProbe.setProviderService(dataProviderService);
    }

}
