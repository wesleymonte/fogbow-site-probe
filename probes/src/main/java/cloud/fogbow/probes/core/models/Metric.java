package cloud.fogbow.probes.core.models;

import cloud.fogbow.probes.core.utils.AppUtil;
import java.sql.Timestamp;
import java.util.List;
import org.json.JSONObject;

/**
 * It is the data structure that represents an observation of some entity at a given {@link
 * #timestamp}. Every observation has {@link #name} and a {@link #observations} as a result.
 */
public class Metric {

    private static final String NAME_JSON_KEY = "name";
    private static final String VALUES_JSON_KEY = "observations";
    private static final String TIMESTAMP_JSON_KEY = "timestamp";
    private static final String HELP_JSON_KEY = "help";
    private String name;
    private List<Observation> observations;
    private Timestamp timestamp;
    private String help;

    public Metric(String name, List<Observation> observations, Timestamp timestamp, String help) {
        this.name = name;
        this.observations = observations;
        this.timestamp = timestamp;
        this.help = help;
    }

    public String getName() {
        return name;
    }

    public List<Observation> getObservations() {
        return observations;
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
        AppUtil.makeBodyField(jsonObject, VALUES_JSON_KEY, this.getObservations());
        AppUtil.makeBodyField(jsonObject, TIMESTAMP_JSON_KEY, this.getTimestamp().getTime());
        AppUtil.makeBodyField(jsonObject, HELP_JSON_KEY, this.getHelp());
        return jsonObject;
    }
}
