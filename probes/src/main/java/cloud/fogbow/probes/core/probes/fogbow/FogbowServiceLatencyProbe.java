package cloud.fogbow.probes.core.probes.fogbow;

import cloud.fogbow.probes.core.PropertiesHolder;
import cloud.fogbow.probes.core.models.Metric;
import cloud.fogbow.probes.core.models.OrderState;
import cloud.fogbow.probes.core.models.ResourceType;
import cloud.fogbow.probes.core.probes.Probe;
import cloud.fogbow.probes.core.services.DataProviderService;
import cloud.fogbow.probes.core.utils.Pair;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FogbowServiceLatencyProbe is responsible for measuring the latency of resource allocation.
 * Latency is measured by the time that elapses between the order being opened ({@link
 * OrderState#OPEN}) until it is available ({@link OrderState#FULFILLED}). This metric measures how
 * long it takes for a resource to be ready.
 */
public class FogbowServiceLatencyProbe implements Probe {

    private static final Logger LOGGER = LogManager.getLogger(FogbowServiceLatencyProbe.class);
    private static final String HELP = "The time that elapses between the order being opened until the order is available.";
    private static final String METRIC_NAME = "latency";
    private static final String RESOURCE_LABEL = "resource";
    protected DataProviderService providerService;

    public List<Metric> getMetrics(Timestamp timestamp) {
        Long[] latencies = this.providerService.getLatencies(timestamp);
        List<Pair<String, Float>> values = toValue(latencies);
        List<Metric> metrics = parseValuesToMetrics(values, timestamp);
        return metrics;
    }

    @Override
    public void populateMetadata(Map<String, String> metadata, Pair<String, Float> p) {
        metadata.put(RESOURCE_LABEL, p.getKey().toLowerCase());
    }

    private List<Pair<String, Float>> toValue(Long[] latencies) {
        Pair<String, Float> computeLatency = new Pair<>(ResourceType.COMPUTE.getValue(),
            (float) latencies[0]);
        Pair<String, Float> networkLatency = new Pair<>(ResourceType.NETWORK.getValue(),
            (float) latencies[1]);
        Pair<String, Float> volumeLatency = new Pair<>(ResourceType.VOLUME.getValue(),
            (float) latencies[2]);
        List<Pair<String, Float>> list = new ArrayList<>();
        for (Pair<String, Float> p : Arrays.asList(computeLatency, networkLatency, volumeLatency)) {
            //To avoid adding data when there are no audits to calculate
            if (p.getValue() != 0) {
                list.add(p);
            }
        }
        return list;
    }

    private List<Metric> parseValuesToMetrics(List<Pair<String, Float>> values,
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
        metadata
            .put(FogbowProbe.targetLabelKey, PropertiesHolder.getInstance().getHostLabelProperty());
        metadata.put(FogbowProbe.probeTargetKey,
            PropertiesHolder.getInstance().getHostAddressProperty());
        Metric m = new Metric(p.getKey().toLowerCase() + "_" + METRIC_NAME, p.getValue(),
            currentTimestamp, HELP, metadata);
        return m;
    }
}
