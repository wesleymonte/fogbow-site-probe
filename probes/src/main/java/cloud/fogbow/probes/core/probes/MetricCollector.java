package cloud.fogbow.probes.core.probes;

import cloud.fogbow.probes.core.models.Metric;
import cloud.fogbow.probes.core.utils.Pair;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * A metric collector. The user of this interface has access to metric generation according to a
 * provided timestamp. The collector is used for only one metric type, which must have a name and a
 * help comment.
 */
public interface MetricCollector {

    /**
     * Generates a list of metrics taking into account a timestamp when analyzing data.
     */
    List<Metric> collect(Timestamp timestamp);

    /**
     * Returns the name of the metric that is collected
     * @return The metric name
     */
    String getMetricName();

    /**
     * Returns a string with a short comment explaining about the metric that is collected
     */
    String getHelp();

    /**
     * Add metadata about metrics according to received data pair
     */
    void populateMetadata(Map<String, String> metadata, Pair<String, Float> p);
}
