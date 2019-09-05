package cloud.fogbow.probes.core.models;

import java.sql.Timestamp;
import java.util.List;
import cloud.fogbow.probes.core.utils.Pair;

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
