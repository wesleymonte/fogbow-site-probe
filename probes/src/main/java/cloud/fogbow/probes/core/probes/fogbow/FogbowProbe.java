package cloud.fogbow.probes.core.probes.fogbow;

import cloud.fogbow.probes.core.models.Metric;
import cloud.fogbow.probes.core.models.Probe;
import cloud.fogbow.probes.core.utils.Pair;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class FogbowProbe extends Probe {

    private static final String probeTargetKey = "target_host";
    private String help;
    private String metricName;
    private String metricValueType;

    FogbowProbe(String targetLabel, String probeTarget, String ftaAddress, String help, String metricName, String metricValueType) {
        super(targetLabel, probeTarget, ftaAddress);
        this.help = help;
        this.metricName = metricName;
        this.metricValueType = metricValueType;
    }

    List<Metric> parseValuesToMetrics(List<Pair<String, Float>> values, Timestamp currentTimestamp) {
        List<Metric> metrics = new ArrayList<>();
        for (Pair<String, Float> p : values) {
            Metric m = parsePairToMetric(p, currentTimestamp);
            metrics.add(m);
        }
        return metrics;
    }

    private Metric parsePairToMetric(Pair<String, Float> p, Timestamp currentTimestamp) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put(metricValueType, p.getKey());
        metadata.put(targetLabelKey, targetLabel);
        metadata.put(probeTargetKey, probeTarget);
        Metric m = new Metric(p.getKey().toLowerCase() + "_" + metricName, p.getValue(), currentTimestamp, help,
            metadata);
        return m;
    }
}
