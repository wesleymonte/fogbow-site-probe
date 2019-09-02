package cloud.fogbow.probes.core.models;

import cloud.fogbow.probes.core.Constants;
import cloud.fogbow.probes.core.fta.FtaSender;
import cloud.fogbow.probes.core.services.DataProviderService;
import java.sql.Timestamp;
import java.util.Properties;
import javax.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class Probe implements Runnable {

    protected Integer PROBE_ID;
    @Autowired
    protected Properties properties;
    @Autowired
    protected DataProviderService providerService;
    protected Timestamp lastTimestampAwake;
    protected boolean firstTimeAwake;
    protected Integer SLEEP_TIME;
    protected String FTA_ADDRESS;
    private static final Logger LOGGER = LogManager.getLogger(Probe.class);

    @PostConstruct
    public void Probe(){
        this.lastTimestampAwake = new Timestamp(System.currentTimeMillis());
        this.PROBE_ID = Integer.valueOf(properties.getProperty(Constants.RESOURCE_AVAILABILITY_PROBE_ID));
        this.SLEEP_TIME = Integer.valueOf(properties.getProperty(Constants.SLEEP_TIME));
        this.FTA_ADDRESS = properties.getProperty(Constants.FMA_ADDRESS);
        this.firstTimeAwake = true;
    }

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
        LOGGER.info(
            "Probe[" + this.PROBE_ID + "] made a observation at [" + observation.getTimestamp()
                .toString() + "]");
        FtaSender.sendObservation(FTA_ADDRESS, observation);
        lastTimestampAwake = currentTimestamp;
        sleep(SLEEP_TIME);
    }

    protected abstract Observation makeObservation(Timestamp timestamp);
}
