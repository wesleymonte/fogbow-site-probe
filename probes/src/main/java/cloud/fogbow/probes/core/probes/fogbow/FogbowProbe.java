package cloud.fogbow.probes.core.probes.fogbow;

import cloud.fogbow.probes.core.PropertiesHolder;
import cloud.fogbow.probes.core.fta.FtaSender;
import cloud.fogbow.probes.core.models.Metric;
import cloud.fogbow.probes.core.probes.Probe;
import cloud.fogbow.probes.core.services.DataProviderService;
import java.sql.Timestamp;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FogbowProbe implements Runnable {

    public static final String probeTargetKey = "target_host";
    public static final String targetLabelKey = "target_label";
    private static final Logger LOGGER = LogManager.getLogger(FogbowProbe.class);

    protected Timestamp lastTimestampAwake;
    protected DataProviderService providerService;
    //To avoid send duplicate metrics from same timestamp
    private Timestamp lastSubmissionTimestamp;

    private Probe probe;

    public FogbowProbe(Probe probe) {
        this.probe = probe;
    }

    @Override
    public void run() {
        LOGGER.info("Running probe [" + Thread.currentThread().getName() + "] ...");

        if (Objects.nonNull(lastTimestampAwake)) {
            if (!lastTimestampAwake.equals(lastSubmissionTimestamp)) {
                LOGGER.info("Current timestamp: " + lastTimestampAwake);
                try {
                    List<Metric> metrics = probe.getMetrics(lastTimestampAwake);
                    LOGGER.info("Made as metrics [" + metrics.size() + "] at [" + lastTimestampAwake
                        .toString() + "]");
                    FtaSender.sendMetrics(PropertiesHolder.getInstance().getFtaAddressProperty(),
                        metrics);
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

    private Timestamp getBiggerTimestamp(List<Metric> metric) {
        Optional<Metric> opt = metric.stream().max(Comparator.comparing(Metric::getTimestamp));
        return opt.map(Metric::getTimestamp).orElse(null);
    }


}
