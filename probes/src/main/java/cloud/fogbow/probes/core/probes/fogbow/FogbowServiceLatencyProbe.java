package cloud.fogbow.probes.core.probes.fogbow;

import cloud.fogbow.probes.core.models.Metric;
import cloud.fogbow.probes.core.models.OrderState;
import cloud.fogbow.probes.core.models.ResourceType;
import cloud.fogbow.probes.core.utils.Pair;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    private static final String RESOURCE_LABEL = "resource";

    public FogbowServiceLatencyProbe(String targetLabel, String probeTarget, String ftaAddress) {
        super(targetLabel, probeTarget, ftaAddress, HELP, METRIC_NAME);
    }

    protected List<Metric> getMetrics(Timestamp currentTimestamp) {
        Long[] latencies = this.providerService.getLatencies(lastTimestampAwake);
        List<Pair<String, Float>> values = toValue(latencies);
        List<Metric> metrics = parseValuesToMetrics(values, currentTimestamp);
        LOGGER.info("Made a metric with name at [" + currentTimestamp.toString() + "]");
        return metrics;
    }

    protected void populateMetadata(Map<String, String> metadata, Pair<String, Float> p) {
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
        for(Pair<String, Float> p : Arrays.asList(computeLatency, networkLatency, volumeLatency)){
            //To avoid adding data when there are no audits to calculate
            if(p.getValue() != 0){
                list.add(p);
            }
        }
        return list;
    }
}
