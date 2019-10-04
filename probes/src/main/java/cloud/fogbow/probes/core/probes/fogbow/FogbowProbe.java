package cloud.fogbow.probes.core.probes.fogbow;

import cloud.fogbow.probes.core.PropertiesHolder;
import cloud.fogbow.probes.core.fta.FtaSender;
import cloud.fogbow.probes.core.models.Metric;
import cloud.fogbow.probes.core.probes.Probe;
import cloud.fogbow.probes.core.services.DataProviderService;
import cloud.fogbow.probes.core.utils.Pair;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class FogbowProbe implements Probe {

    protected static final String targetLabelKey = "target_label";
    private static final Logger LOGGER = LogManager.getLogger(FogbowProbe.class);
    private static final String probeTargetKey = "target_host";
    protected Timestamp lastTimestampAwake;
    protected DataProviderService providerService;
    private String help;
    private String metricName;
    //To avoid send duplicate metrics from same timestamp
    private Timestamp lastSubmissionTimestamp;

    public FogbowProbe(String help, String metricName) {
        this.help = help;
        this.metricName = metricName;
    }

    @Override
    public void run() {
        LOGGER.info("Running probe [" + Thread.currentThread().getName() + "] ...");

        if (Objects.nonNull(lastTimestampAwake)) {
            if (!lastTimestampAwake.equals(lastSubmissionTimestamp)) {
                LOGGER.info("Current timestamp: " + lastTimestampAwake);
                try {
                    List<Metric> metrics = getMetrics(lastTimestampAwake);
                    LOGGER.info("Made as metrics [" + metrics.size() + "] at [" + lastTimestampAwake
                        .toString() + "]");
                    FtaSender.sendMetrics(PropertiesHolder.getInstance().getFtaAddressProperty(), metrics);
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


    List<Metric> parseValuesToMetrics(List<Pair<String, Float>> values,
        Timestamp currentTimestamp) {
        List<Metric> metrics = new ArrayList<>();
        for (Pair<String, Float> p : values) {
            Metric m = parsePairToMetric(p, currentTimestamp);
            metrics.add(m);
        }
        return metrics;
    }

    private Metric parsePairToMetric(Pair<String, Float> p, Timestamp currentTimestamp) {
        Map<String, String> metadata = new HashMap<>();
        populateMetadata(metadata, p);
        metadata.put(targetLabelKey, PropertiesHolder.getInstance().getHostLabelProperty());
        metadata.put(probeTargetKey, PropertiesHolder.getInstance().getHostAddressProperty());
        Metric m = new Metric(p.getKey().toLowerCase() + "_" + metricName, p.getValue(),
            currentTimestamp, help, metadata);
        return m;
    }

    protected abstract void populateMetadata(Map<String, String> metadata, Pair<String, Float> p);

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
