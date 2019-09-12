package cloud.fogbow.probes.core.models;

import cloud.fogbow.probes.core.fta.FtaSender;
import cloud.fogbow.probes.core.services.DataProviderService;
import cloud.fogbow.probes.core.utils.AppUtil;
import cloud.fogbow.probes.core.utils.Pair;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    protected String VALUE_TYPE_KEY;
    protected String PROBE_TYPE;
    protected String HELP;

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
            FtaSender.sendMetrics(FTA_ADDRESS, metric);
        } catch (IllegalArgumentException e) {
            LOGGER.error(
                "Error while probe running at [" + currentTimestamp
                    + "]: " + e.getMessage());
        }
        lastTimestampAwake = currentTimestamp;
        AppUtil.sleep(SLEEP_TIME);
    }

    protected void parseValuesToMetrics(List<Metric> metrics, List<Pair<String, Float>> values, Timestamp currentTimestamp){
        for(Pair<String, Float> p : values){
            Map<String, String> metadata = new HashMap<>();
            metadata.put(VALUE_TYPE_KEY, p.getKey());
            Metric m = new Metric(PROBE_TYPE, p.getValue(), currentTimestamp, HELP, metadata);
            metrics.add(m);
        }
    }

    protected abstract List<Metric> getMetrics(Timestamp timestamp);

    public void setProviderService(DataProviderService providerService) {
        this.providerService = providerService;
    }
}
