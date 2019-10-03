package cloud.fogbow.probes.core.models;

import cloud.fogbow.probes.core.fta.FtaSender;
import cloud.fogbow.probes.core.services.DataProviderService;
import java.sql.Timestamp;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * It is an entity in charge of making observations at every moment of time ). All observations are
 * sent to the Fogbow Telemetry Aggregator by address {@link #ftaAddress} using {@link FtaSender}.
 */
public abstract class Probe implements Runnable {

    protected static final String targetLabelKey = "target_label";
    private static final Logger LOGGER = LogManager.getLogger(Probe.class);
    protected DataProviderService providerService;
    protected Timestamp lastTimestampAwake;
    protected String targetLabel;
    protected String probeTarget;
    private String ftaAddress;
    //To avoid send duplicate metrics from same timestamp
    private Timestamp lastSubmissionTimestamp;

    public Probe(String targetLabel, String probeTarget, String ftaAddress) {
        this.targetLabel = targetLabel;
        this.probeTarget = probeTarget;
        this.ftaAddress = ftaAddress;
    }

    @Override
    public void run() {
        LOGGER.info("Running probe [" + Thread.currentThread().getName() + "] ...");

        if (Objects.nonNull(lastTimestampAwake)) {
            if (!lastTimestampAwake.equals(lastSubmissionTimestamp)) {
                LOGGER.info("Current timestamp: " + lastTimestampAwake);
                try {
                    List<Metric> metrics = getMetrics(lastTimestampAwake);
                    LOGGER.info("Made as metrics [" + metrics.size() + "] at [" + lastTimestampAwake.toString() + "]");
                    FtaSender.sendMetrics(ftaAddress, metrics);
                    lastSubmissionTimestamp = lastTimestampAwake;
                    Timestamp newTimestamp = getBiggerTimestamp(metrics);
                    if (Objects.nonNull(newTimestamp)) {
                        lastTimestampAwake = newTimestamp;
                    }
                } catch (Exception e) {
                    LOGGER.error("Error while probe running at [" + lastTimestampAwake + "]: " + e
                        .getMessage());
                }
            } else {
                LOGGER.info("No new data to analyze");
                lastTimestampAwake = providerService.getMaxTimestampFromAuditOrders();
            }
        } else {
            LOGGER.info("Getting a timestamp for performing database queries...");
            lastTimestampAwake = providerService.getMaxTimestampFromAuditOrders();
        }
    }

    protected abstract List<Metric> getMetrics(Timestamp timestamp);

    public void setProviderService(DataProviderService providerService) {
        this.providerService = providerService;
    }

    private Timestamp getBiggerTimestamp(List<Metric> metric) {
        Optional<Metric> opt = metric.stream().max(Comparator.comparing(Metric::getTimestamp));
        if (opt.isPresent()) {
            return opt.get().getTimestamp();
        }
        return null;
    }
}
