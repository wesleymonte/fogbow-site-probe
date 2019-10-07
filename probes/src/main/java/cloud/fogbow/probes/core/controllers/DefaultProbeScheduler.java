package cloud.fogbow.probes.core.controllers;

import cloud.fogbow.probes.core.Constants;
import cloud.fogbow.probes.core.PropertiesHolder;
import cloud.fogbow.probes.core.controllers.threadfactory.DefaultThreadFactory;
import cloud.fogbow.probes.core.probes.Probe;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DefaultProbeScheduler implements ProbeScheduler {

    private static final Logger LOGGER = LogManager.getLogger(DefaultProbeScheduler.class);
    private static final int POOL_SIZE = 5;
    private static final String THREAD_NAME_PREFIX = "Probe-";
    private ScheduledExecutorService scheduled;

    public DefaultProbeScheduler() {
        this.scheduled = new ScheduledThreadPoolExecutor(POOL_SIZE,
            new DefaultThreadFactory(THREAD_NAME_PREFIX));
    }

    @Override
    public void submitProbes(List<Probe> probes) {
        for(Probe p : probes){
            submitProbe(p);
        }
    }

    private void submitProbe(Probe probe) {
        long delay = Long.parseLong(PropertiesHolder.getInstance().getProperty(Constants.DELAY));
        long initialDelay = 5000;
        LOGGER.debug(
            "Scheduling Probe: INITIAL_DELAY [" + initialDelay + "]; DELAY ["
                + delay + "]");
        scheduled.scheduleWithFixedDelay(probe, initialDelay, delay,
            TimeUnit.MILLISECONDS);
    }
}
