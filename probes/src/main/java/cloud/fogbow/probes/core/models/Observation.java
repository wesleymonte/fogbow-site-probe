package cloud.fogbow.probes.core.models;

import cloud.fogbow.probes.core.utils.AppUtil;
import java.sql.Timestamp;
import java.util.List;
import org.json.JSONObject;

/**
 * It is the data structure that represents an observation of some entity at a given {@link
 * #timestamp}. Every observation has {@link #label} and a {@link #values} as a result.
 */
public class Observation {

    private static final String METRIC_LABEL_JSON_KEY = "label";
    private static final String VALUES_JSON_KEY = "values";
    private static final String TIMESTAMP_JSON_KEY = "timestamp";
    private static final String HELP_JSON_KEY = "timestamp";
    private String label;
    private List<Value> values;
    private Timestamp timestamp;
    private String help;

    public Observation(String label, List<Value> values, Timestamp timestamp, String help) {
        this.label = label;
        this.values = values;
        this.timestamp = timestamp;
        this.help = help;
    }

    public String getLabel() {
        return label;
    }

    public List<Value> getValues() {
        return values;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getHelp() {
        return help;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        AppUtil.makeBodyField(jsonObject, METRIC_LABEL_JSON_KEY, this.getLabel());
        AppUtil.makeBodyField(jsonObject, VALUES_JSON_KEY, this.getValues());
        AppUtil.makeBodyField(jsonObject, TIMESTAMP_JSON_KEY, this.getTimestamp().getTime());
        AppUtil.makeBodyField(jsonObject, HELP_JSON_KEY, this.getHelp());
        return jsonObject;
    }
}
