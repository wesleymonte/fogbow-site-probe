package cloud.fogbow.probes.core.models;

import cloud.fogbow.probes.core.fta.FtaSender;
import cloud.fogbow.probes.core.services.DataProviderService;
import cloud.fogbow.probes.core.utils.AppUtil;
import java.sql.Timestamp;
import java.util.List;
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
    protected DataProviderService providerService;
    protected Timestamp lastTimestampAwake;
    protected boolean firstTimeAwake;
    protected Integer SLEEP_TIME;
    protected String FTA_ADDRESS;

    public Probe(Integer sleepTime, String ftaAddress) {
        this.lastTimestampAwake = new Timestamp(System.currentTimeMillis());
        this.SLEEP_TIME = sleepTime;
        this.FTA_ADDRESS = ftaAddress;
        this.firstTimeAwake = true;
    }

    public void run() {
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        try {
            List<Metric> metric = getMetrics(currentTimestamp);
//            LOGGER.info(
//                "Probe got a metric at [" + metric.getTimestamp().toString()
//                    + "]");
            FtaSender.sendMetrics(FTA_ADDRESS, metric);
        } catch (IllegalArgumentException e) {
            LOGGER.error(
                "Error while probe running at [" + currentTimestamp
                    + "]: " + e.getMessage());
        }
        lastTimestampAwake = currentTimestamp;
        AppUtil.sleep(SLEEP_TIME);
    }

    protected abstract List<Metric> getMetrics(Timestamp timestamp);

    public void setProviderService(DataProviderService providerService) {
        this.providerService = providerService;
    }
}
