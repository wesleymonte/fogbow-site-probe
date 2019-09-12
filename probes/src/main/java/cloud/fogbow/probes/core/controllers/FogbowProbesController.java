package cloud.fogbow.probes.core.controllers;

import cloud.fogbow.probes.core.Constants;
import cloud.fogbow.probes.core.probes.FogbowResourceAvailabilityProbe;
import cloud.fogbow.probes.core.probes.FogbowServiceLatencyProbe;
import cloud.fogbow.probes.core.probes.FogbowServiceReachabilityProbe;
import cloud.fogbow.probes.core.probes.FogbowServiceSuccessRateProbe;
import cloud.fogbow.probes.core.services.DataProviderService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FogbowProbesController {

    private FogbowResourceAvailabilityProbe resourceAvailabilityProbe;
    private FogbowServiceLatencyProbe serviceLatencyProbe;
    private FogbowServiceSuccessRateProbe serviceSuccessRateProbe;
    private FogbowServiceReachabilityProbe serviceReachabilityProbe;

    private List<Thread> pool = new LinkedList<>();
    private boolean isStarted = false;

    private static final Logger LOGGER = LogManager.getLogger(FogbowProbesController.class);

    private Properties properties;

    public FogbowProbesController(Properties properties) {
        this.properties = properties;
    }

    public void init(DataProviderService dataProviderService){
        Integer sleepTime = Integer.valueOf(properties.getProperty(Constants.SLEEP_TIME));
        String ftaAddress = properties.getProperty(Constants.FTA_ADDRESS);
        LOGGER.info("Defining Fogbow Probes:\n\tSleep time [" + sleepTime + "]\n\tFogbow Telemetry Aggregator Address [" + ftaAddress + "]");
        this.resourceAvailabilityProbe = new FogbowResourceAvailabilityProbe(sleepTime, ftaAddress);
        this.serviceLatencyProbe = new FogbowServiceLatencyProbe(sleepTime, ftaAddress);
        this.serviceSuccessRateProbe = new FogbowServiceSuccessRateProbe(sleepTime, ftaAddress);
        this.serviceReachabilityProbe = new FogbowServiceReachabilityProbe(sleepTime, ftaAddress,
            properties.getProperty(Constants.AS_ENDPOINT), properties.getProperty(Constants.RAS_ENDPOINT),
            properties.getProperty(Constants.FNS_ENDPOINT), properties.getProperty(Constants.MS_ENDPOINT));
        this.setProviderService(dataProviderService);
    }

    private void createThreads(){
        Thread firstProbe = new Thread(resourceAvailabilityProbe, FogbowResourceAvailabilityProbe.THREAD_NAME);
        Thread secondProbe = new Thread(serviceLatencyProbe, FogbowServiceLatencyProbe.THREAD_NAME);
        Thread thirdProbe = new Thread(serviceSuccessRateProbe, FogbowServiceSuccessRateProbe.THREAD_NAME);
        Thread fourthProbe = new Thread(serviceReachabilityProbe, FogbowServiceReachabilityProbe.THREAD_NAME);
        pool = new ArrayList<>(
            Arrays.asList(firstProbe,
                secondProbe,
                thirdProbe,
                fourthProbe));
    }

    public void startAll(){
        LOGGER.info("Starting Fogbow Probes Threads...");
        if(!isStarted) {
            createThreads();
            for (Thread t : pool) {
                t.start();
            }
            isStarted = true;
        }
    }

    private void setProviderService(DataProviderService dataProviderService){
        resourceAvailabilityProbe.setProviderService(dataProviderService);
        serviceLatencyProbe.setProviderService(dataProviderService);
        serviceSuccessRateProbe.setProviderService(dataProviderService);
        serviceReachabilityProbe.setProviderService(dataProviderService);
    }

}
