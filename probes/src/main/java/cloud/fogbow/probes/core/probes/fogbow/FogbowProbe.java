package cloud.fogbow.probes.core.probes.fogbow;

import cloud.fogbow.probes.core.models.Metric;
import cloud.fogbow.probes.core.models.Probe;
import cloud.fogbow.probes.core.utils.Pair;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class FogbowProbe extends Probe {

    private String help;
    private String metricName;
    private String metricValueType;

    FogbowProbe(String ftaAddress, String help, String metricName,
        String metricValueType, String threadName) {
        super(ftaAddress, threadName);
        this.help = help;
        this.metricName = metricName;
        this.metricValueType = metricValueType;
    }

    void parseValuesToMetrics(List<Pair<String, Float>> values, Timestamp currentTimestamp,
        List<Metric> metrics) {
        for (Pair<String, Float> p : values) {
            Map<String, String> metadata = new HashMap<>();
            metadata.put(metricValueType, p.getKey());
            Metric m = new Metric(metricName, p.getValue(), currentTimestamp, help, metadata);
            metrics.add(m);
        }
    }
}
