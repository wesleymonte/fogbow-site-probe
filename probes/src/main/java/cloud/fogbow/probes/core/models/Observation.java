package cloud.fogbow.probes.core.models;

import java.sql.Timestamp;
import javafx.util.Pair;

public class Observation {
    private String label;
    private Pair<String, String> values;
    private Timestamp timestamp;

    public Observation(String label, Pair<String, String> values, Timestamp timestamp) {
        this.label = label;
        this.values = values;
        this.timestamp = timestamp;
    }

    public String getLabel() {
        return label;
    }

    public Pair<String, String> getValues() {
        return values;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}
