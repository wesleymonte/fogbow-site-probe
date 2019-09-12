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

/**
 * It is an entity in charge of making observations at every moment of time ({@link #sleepTime}).
 * All observations are sent to the Fogbow Telemetry Aggregator by address {@link #ftaAddress} using
 * {@link FtaSender}.
 */
public abstract class Probe implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger(Probe.class);
    protected DataProviderService providerService;
    protected Timestamp lastTimestampAwake;
    protected boolean firstTimeAwake;
    protected Integer sleepTime;
    protected String ftaAddress;
    protected String metricValueType;
    protected String metricName;
    protected String help;

    public Probe(Integer sleepTime, String ftaAddress) {
        this.lastTimestampAwake = new Timestamp(System.currentTimeMillis());
        this.sleepTime = sleepTime;
        this.ftaAddress = ftaAddress;
        this.firstTimeAwake = true;
    }

    public void run() {
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        try {
            List<Metric> metric = getMetrics(currentTimestamp);
            FtaSender.sendMetrics(ftaAddress, metric);
        } catch (IllegalArgumentException e) {
            LOGGER.error(
                "Error while probe running at [" + currentTimestamp + "]: " + e.getMessage());
        }
        lastTimestampAwake = currentTimestamp;
        AppUtil.sleep(sleepTime);
    }

    protected void parseValuesToMetrics(List<Metric> metrics, List<Pair<String, Float>> values,
        Timestamp currentTimestamp) {
        for (Pair<String, Float> p : values) {
            Map<String, String> metadata = new HashMap<>();
            metadata.put(metricValueType, p.getKey());
            Metric m = new Metric(metricName, p.getValue(), currentTimestamp, help, metadata);
            metrics.add(m);
        }
    }

    protected abstract List<Metric> getMetrics(Timestamp timestamp);

    public void setProviderService(DataProviderService providerService) {
        this.providerService = providerService;
    }
}
