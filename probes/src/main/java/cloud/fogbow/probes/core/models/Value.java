package cloud.fogbow.probes.core.models;

import org.json.JSONObject;

/**
 * The value entity is basically a structure for storing the result of a measurement. It always has
 * a description for measurement and measurement.
 */
public class Value {

    private static final String DESCRIPTION_JSON_KEY = "description";
    private static final String MEASUREMENT_JSON_KEY = "measurement";
    private String description;
    private Float measurement;

    public Value(String description, Float measurement) {
        this.description = description;
        this.measurement = measurement;
    }

    public String getDescription() {
        return description;
    }

    public Float getMeasurement() {
        return measurement;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put(DESCRIPTION_JSON_KEY, this.getDescription());
        json.put(MEASUREMENT_JSON_KEY, this.getMeasurement());
        return json;
    }
}
