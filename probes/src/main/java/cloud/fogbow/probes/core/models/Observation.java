package cloud.fogbow.probes.core.models;

import org.json.JSONObject;

/**
 * The value entity is basically a structure for storing the result of a measurement. It always has
 * a label for measurement and measurement value.
 */
public class Observation {

    private static final String LABEL_JSON_KEY = "label";
    private static final String MEASUREMENT_JSON_KEY = "measurement";
    private String label;
    private Float measurement;

    public Observation(String label, Float measurement) {
        this.label = label;
        this.measurement = measurement;
    }

    public String getLabel() {
        return label;
    }

    public Float getMeasurement() {
        return measurement;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put(LABEL_JSON_KEY, this.getLabel());
        json.put(MEASUREMENT_JSON_KEY, this.getMeasurement());
        return json;
    }
}
