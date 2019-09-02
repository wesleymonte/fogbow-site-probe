package cloud.fogbow.probes.core.models;

import cloud.fogbow.probes.core.fta.FtaSender;
import cloud.fogbow.probes.core.probes.FogbowResourceAvailabilityProbe;
import cloud.fogbow.probes.core.services.DataProviderService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.util.Properties;

public abstract class Probe implements Runnable {

    @Autowired
    protected Properties properties;

    protected Timestamp lastTimestampAwake;
    protected Integer probeId;
    protected boolean firstTimeAwake;
    @Autowired
    protected DataProviderService providerService;

    protected Integer SLEEP_TIME;
    protected String FTA_ADDRESS;

    private static final Logger LOGGER = LogManager.getLogger(Probe.class);

    protected void sleep(int sleepTime) {
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public void run() {
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        Observation observation = makeObservation(currentTimestamp);
        LOGGER.info("Probe[" + this.probeId + "] made a observation at [" + observation.getTimestamp().toString() + "]");
        FtaSender.sendObservation(FTA_ADDRESS, observation);
        lastTimestampAwake = currentTimestamp;
        sleep(SLEEP_TIME);
    }

    protected abstract Observation makeObservation(Timestamp timestamp);
}
