package cloud.fogbow.probes.core.probes;

import cloud.fogbow.probes.core.fta.FtaSender;
import cloud.fogbow.probes.core.models.Metric;
import cloud.fogbow.probes.core.utils.Pair;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * It is an entity in charge of making observations at every moment of time ). All observations are
 * sent to the Fogbow Telemetry Aggregator by address {@link #ftaAddress} using {@link FtaSender}.
 */
public interface MetricCollector {

    List<Metric> getMetrics(Timestamp timestamp);
    String getMetricName();
    String getHelp();
    void populateMetadata(Map<String, String> metadata, Pair<String, Float> p);
}
