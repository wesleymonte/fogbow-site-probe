package cloud.fogbow.probes.core.probes.fogbow;

import cloud.fogbow.probes.core.models.Metric;
import cloud.fogbow.probes.core.models.Probe;
import cloud.fogbow.probes.core.utils.Pair;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract class FogbowProbe extends Probe {

    private String help;
    private String metricName;
    private String metricValueType;

    public FogbowProbe(Integer sleepTime, String ftaAddress, String help, String metricName,
        String metricValueType) {
        super(sleepTime, ftaAddress);
        this.help = help;
        this.metricName = metricName;
        this.metricValueType = metricValueType;
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
