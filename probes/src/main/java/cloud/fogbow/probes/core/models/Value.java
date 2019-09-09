package cloud.fogbow.probes.core.models;

public class Value {

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
}
