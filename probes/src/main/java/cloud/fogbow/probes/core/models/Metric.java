package cloud.fogbow.probes.core.models;

import cloud.fogbow.probes.core.utils.AppUtil;
import java.sql.Timestamp;
import java.util.Map;
import org.json.JSONObject;

/**
 * It is the data structure that represents an observation of some entity at a given {@link
 * #timestamp}. Every observation has {@link #name} and a Value as a result.
 */
public class Metric {

    private static final String NAME_JSON_KEY = "name";
    private static final String VALUES_JSON_KEY = "value";
    private static final String TIMESTAMP_JSON_KEY = "timestamp";
    private static final String HELP_JSON_KEY = "help";
    private static final String METADATA_JSON_KEY = "metadata";
    private String name;
    private String help;
    private Timestamp timestamp;
    private Float value;
    private Map<String, String> metadata;

    public Metric(String name, Float value, Timestamp timestamp, String help,
        Map<String, String> metadata) {
        this.name = name;
        this.value = value;
        this.timestamp = timestamp;
        this.help = help;
        this.metadata = metadata;
    }

    public String getName() {
        return name;
    }

    public Float getValue() {
        return value;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getHelp() {
        return help;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        AppUtil.makeBodyField(jsonObject, NAME_JSON_KEY, this.getName());
        AppUtil.makeBodyField(jsonObject, VALUES_JSON_KEY, this.getValue());
        AppUtil.makeBodyField(jsonObject, TIMESTAMP_JSON_KEY, this.getTimestamp().getTime());
        AppUtil.makeBodyField(jsonObject, HELP_JSON_KEY, this.getHelp());
        AppUtil.makeBodyField(jsonObject, METADATA_JSON_KEY, this.getMetadata());
        return jsonObject;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }
}
