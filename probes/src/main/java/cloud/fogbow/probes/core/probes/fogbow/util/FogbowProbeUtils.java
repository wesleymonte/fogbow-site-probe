package cloud.fogbow.probes.core.probes.fogbow.util;

import cloud.fogbow.probes.core.PropertiesHolder;
import cloud.fogbow.probes.core.models.Metric;
import cloud.fogbow.probes.core.probes.MetricCollector;
import cloud.fogbow.probes.core.probes.fogbow.FogbowProbe;
import cloud.fogbow.probes.core.utils.Pair;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FogbowProbeUtils {

    public static Metric parsePairToMetric(Pair<String, Float> p, String metricName, String help,
        Timestamp currentTimestamp, Map<String, String> metadata) {
        metadata
            .put(FogbowProbe.targetLabelKey, PropertiesHolder.getInstance().getHostLabelProperty());
        metadata.put(FogbowProbe.probeTargetKey,
            PropertiesHolder.getInstance().getHostAddressProperty());
        Metric m = new Metric(p.getKey().toLowerCase() + "_" + metricName, p.getValue(),
            currentTimestamp, help, metadata);
        return m;
    }

    public static List<Metric> parsePairsToMetrics(MetricCollector metricCollector, List<Pair<String, Float>> values,
        String cloudName, Timestamp timestamp) {
        List<Metric> metrics = new ArrayList<>();
        for (Pair<String, Float> p : values) {
            Map<String, String> metadata = new HashMap<>();
            metadata.put(FogbowProbe.cloudNameKey, cloudName);
            metricCollector.populateMetadata(metadata, p);
            Metric m = FogbowProbeUtils.parsePairToMetric(p, metricCollector.getMetricName(), metricCollector
                .getHelp(), timestamp, metadata);
            metrics.add(m);
        }
        return metrics;
    }
}
