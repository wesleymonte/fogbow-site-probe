package cloud.fogbow.probes.core.models;

import cloud.fogbow.probes.core.fta.FtaSender;
import cloud.fogbow.probes.core.services.DataProviderService;
import java.sql.Timestamp;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * It is an entity in charge of making observations at every moment of time ). All observations are
 * sent to the Fogbow Telemetry Aggregator by address {@link #ftaAddress} using {@link FtaSender}.
 */
public abstract class Probe implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger(Probe.class);
    protected static final String targetLabelKey = "target_label";
    protected DataProviderService providerService;
    protected Timestamp lastTimestampAwake;
    protected String targetLabel;
    protected String probeTarget;
    private String ftaAddress;

    public Probe(String targetLabel, String probeTarget, String ftaAddress) {
        this.targetLabel = targetLabel;
        this.probeTarget = probeTarget;
        this.lastTimestampAwake = new Timestamp(System.currentTimeMillis());
        this.ftaAddress = ftaAddress;
    }

    @Override
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
    }

    protected abstract List<Metric> getMetrics(Timestamp timestamp);

    public void setProviderService(DataProviderService providerService) {
        this.providerService = providerService;
    }

}
