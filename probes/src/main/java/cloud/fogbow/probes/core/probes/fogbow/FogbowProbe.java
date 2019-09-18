package cloud.fogbow.probes.core.probes.fogbow;

import cloud.fogbow.probes.core.models.Metric;
import cloud.fogbow.probes.core.models.Probe;
import cloud.fogbow.probes.core.utils.Pair;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class FogbowProbe extends Probe {

    protected String metricValueType;
    protected String metricName;

    public FogbowProbe(Integer sleepTime, String ftaAddress) {
        super(sleepTime, ftaAddress);
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
}
