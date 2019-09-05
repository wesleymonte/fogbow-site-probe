package cloud.fogbow.probes.core.models;

import cloud.fogbow.probes.core.utils.Pair;
import java.sql.Timestamp;
import java.util.List;

/**
 * It is the data structure that represents an observation of some entity at a given {@link
 * #timestamp}. Every observation has {@link #label} and a {@link #values} as a result.
 */
public class Observation {

    private String label;
    private List<Pair<String, Float>> values;
    private Timestamp timestamp;

    public Observation(String label, List<Pair<String, Float>> values, Timestamp timestamp) {
        this.label = label;
        this.values = values;
        this.timestamp = timestamp;
    }

    public String getLabel() {
        return label;
    }

    public List<Pair<String, Float>> getValues() {
        return values;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}
