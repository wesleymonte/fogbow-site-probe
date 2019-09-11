package cloud.fogbow.probes.core.models;

import cloud.fogbow.probes.core.Constants;
import cloud.fogbow.probes.core.fta.FtaSender;
import cloud.fogbow.probes.core.services.DataProviderService;
import cloud.fogbow.probes.core.utils.AppUtil;
import java.sql.Timestamp;
import java.util.Properties;
import javax.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * It is an entity in charge of making observations at every moment of time ({@link #SLEEP_TIME}).
 * All observations are sent to the Fogbow Telemetry Aggregator by address {@link #FTA_ADDRESS}
 * using {@link FtaSender}.
 */
public abstract class Probe implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger(Probe.class);
    protected Integer PROBE_ID;
    @Autowired
    protected Properties properties;
    @Autowired
    protected DataProviderService providerService;
    protected Timestamp lastTimestampAwake;
    protected boolean firstTimeAwake;
    protected Integer SLEEP_TIME;
    protected String FTA_ADDRESS;

    @PostConstruct
    public void Probe() {
        this.lastTimestampAwake = new Timestamp(System.currentTimeMillis());
        this.SLEEP_TIME = Integer.valueOf(properties.getProperty(Constants.SLEEP_TIME));
        this.FTA_ADDRESS = properties.getProperty(Constants.FMA_ADDRESS);
        this.firstTimeAwake = true;
    }

    public void run() {
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        try {
            Metric metric = getMetric(currentTimestamp);
            LOGGER.info(
                "Probe[" + this.PROBE_ID + "] made a metric at [" + metric.getTimestamp()
                    .toString() + "]");
            FtaSender.sendObservation(FTA_ADDRESS, metric);
        } catch (IllegalArgumentException e) {
            LOGGER.error(
                "Error while probe[" + PROBE_ID + "] making a observation at [" + currentTimestamp
                    + "]: " + e.getMessage());
        }
        lastTimestampAwake = currentTimestamp;
        AppUtil.sleep(SLEEP_TIME);
    }

    protected abstract Metric getMetric(Timestamp timestamp);
}
