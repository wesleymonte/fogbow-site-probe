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

    public Probe(String targetLabel, String probeTarget, String ftaAddress) {
        this.targetLabel = targetLabel;
        this.probeTarget = probeTarget;
        this.lastTimestampAwake = providerService.getMaxTimestampFromAuditOrders();
        this.ftaAddress = ftaAddress;
    }

    @Override
    public void run() {
        if(Objects.isNull(lastTimestampAwake)){
            lastTimestampAwake = providerService.getMaxTimestampFromAuditOrders();
        } else {
            try {
                List<Metric> metric = getMetrics(lastTimestampAwake);
                Timestamp currentTimestamp = getBiggerTimestamp(metric);
                if (Objects.nonNull(currentTimestamp)) {
                    lastTimestampAwake = currentTimestamp;
                }
                FtaSender.sendMetrics(ftaAddress, metric);
            } catch (IllegalArgumentException e) {
                LOGGER.error(
                    "Error while probe running at [" + lastTimestampAwake + "]: " + e.getMessage());
            }
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
