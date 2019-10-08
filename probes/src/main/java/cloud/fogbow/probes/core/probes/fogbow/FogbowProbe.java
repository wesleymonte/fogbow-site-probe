package cloud.fogbow.probes.core.probes.fogbow;

import cloud.fogbow.probes.core.PropertiesHolder;
import cloud.fogbow.probes.fta.FtaSender;
import cloud.fogbow.probes.core.models.Metric;
import cloud.fogbow.probes.core.probes.MetricCollector;
import cloud.fogbow.probes.core.probes.Probe;
import cloud.fogbow.probes.provider.DataProviderService;
import java.sql.Timestamp;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FogbowProbe implements Probe {

    public static final String probeTargetKey = "target_host";
    public static final String targetLabelKey = "target_label";
    private static final Logger LOGGER = LogManager.getLogger(FogbowProbe.class);

    private Timestamp lastTimestampAwake;

    private DataProviderService providerService;
    //To avoid send duplicate metrics from same timestamp
    private Timestamp lastSubmissionTimestamp;

    private MetricCollector metricCollector;

    public FogbowProbe(MetricCollector metricCollector, DataProviderService providerService) {
        this.metricCollector = metricCollector;
        this.providerService = providerService;
        this.lastTimestampAwake = providerService.getMaxTimestampFromAuditOrders();
    }

    @Override
    public void run() {
        LOGGER.info("Running probe [" + Thread.currentThread().getName() + "] ...");

        if (Objects.nonNull(lastTimestampAwake) && !lastTimestampAwake.equals(lastSubmissionTimestamp)) {
            LOGGER.info("Last Timestamp Awake: " + lastTimestampAwake);
            LOGGER.debug("Last Submission Timestamp: " + lastSubmissionTimestamp);
            try {
                List<Metric> metrics = metricCollector.collect(lastTimestampAwake);
                LOGGER.info("Metrics [" + metrics.size() + "] created at [" + lastTimestampAwake + "]");
                if (metrics.size() > 0) {
                    FtaSender.sendMetrics(PropertiesHolder.getInstance().getFtaAddressProperty(), metrics);
                    lastSubmissionTimestamp = lastTimestampAwake;
                    lastTimestampAwake = getBiggerTimestamp(metrics);
                }
            } catch (Exception e) {
                LOGGER.error(
                    "Error while probe running at [" + lastTimestampAwake + "]: " + e.getMessage());
            }
        } else {
            LOGGER.info("No data to analyze");
            LOGGER.info("Getting a timestamp from database for performing database queries...");
            lastTimestampAwake = providerService.getMaxTimestampFromAuditOrders();
        }
    }

    private Timestamp getBiggerTimestamp(List<Metric> metric) {
        Optional<Metric> opt = metric.stream().max(Comparator.comparing(Metric::getTimestamp));
        return opt.map(Metric::getTimestamp).orElse(null);
    }


}
