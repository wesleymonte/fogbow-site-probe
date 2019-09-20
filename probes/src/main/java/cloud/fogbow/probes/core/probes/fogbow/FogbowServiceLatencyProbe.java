package cloud.fogbow.probes.core.probes.fogbow;

import cloud.fogbow.probes.core.models.Metric;
import cloud.fogbow.probes.core.models.OrderState;
import cloud.fogbow.probes.core.utils.Pair;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FogbowServiceLatencyProbe is responsible for measuring the latency of resource allocation.
 * Latency is measured by the time that elapses between the order being opened ({@link
 * OrderState#OPEN}) until it is available ({@link OrderState#FULFILLED}). This metric measures how
 * long it takes for a resource to be ready.
 */
public class FogbowServiceLatencyProbe extends FogbowProbe {

    private static final Logger LOGGER = LogManager.getLogger(FogbowServiceLatencyProbe.class);
    private static final String HELP = "The time that elapses between the order being opened until the order is available.";
    private static final String METRIC_NAME = "latency";
    private static final String METRIC_VALUE_TYPE = "resource";
    private static final String COMPUTE_JSON_KEY = "COMPUTE";
    private static final String NETWORK_JSON_KEY = "NETWORK";
    private static final String VOLUME_JSON_KEY = "VOLUME";

    public FogbowServiceLatencyProbe(String ftaAddress) {
        super(ftaAddress, HELP, METRIC_NAME, METRIC_VALUE_TYPE);
    }

    protected List<Metric> getMetrics(Timestamp currentTimestamp) {
        Long[] latencies = this.providerService.getLatencies(currentTimestamp, firstTimeAwake);
        List<Pair<String, Float>> values = toValue(latencies);
        List<Metric> metrics = new ArrayList<>();
        parseValuesToMetrics(values, currentTimestamp, metrics);
        LOGGER.info("Made a metric with name at [" + currentTimestamp.toString() + "]");
        return metrics;
    }

    private List<Pair<String, Float>> toValue(Long[] latencies) {
        Pair<String, Float> computeLatency = new Pair<>(COMPUTE_JSON_KEY, (float) latencies[0]);
        Pair<String, Float> networkLatency = new Pair<>(NETWORK_JSON_KEY, (float) latencies[1]);
        Pair<String, Float> volumeLatency = new Pair<>(VOLUME_JSON_KEY, (float) latencies[2]);
        List<Pair<String, Float>> list = new ArrayList<>(
            Arrays.asList(computeLatency, networkLatency, volumeLatency));
        return list;
    }
}
