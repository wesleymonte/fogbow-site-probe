/**
 * <b>ATMOSPHERE</b> - http://www.atmosphere-eubrazil.eu/
 * ***
 * <p>
 * <b>Trustworthiness Monitoring & Assessment Framework</b>
 * Component: Monitor - Client
 * <p>
 * Repository: https://github.com/eubr-atmosphere/tma-framework
 * License: https://github.com/eubr-atmosphere/tma-framework/blob/master/LICENSE
 * <p>
 * <p>
 */
package eu.atmosphere.tmaf.monitor.message;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * tma-m_schema_0_3::Observation
 * <p>
 * <p>
 * @author Nuno Antunes <nmsa@dei.uc.pt>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "time",
    "value"
})
public class Observation implements Serializable {

    private final static long serialVersionUID = -8996252602629896220L;

    /**
     *
     * (Required)
     * <p>
     */
    @JsonProperty("time")
    private long time = -1L;
    @JsonProperty("value")
    private double value = 0.0D;

    /**
     * No args constructor for use in serialization
     * <p>
     */
    public Observation() {
    }

    /**
     *
     * @param time
     * @param value
     */
    public Observation(long time, double value) {
        super();
        this.time = time;
        this.value = value;
    }

    /**
     *
     * (Required)
     * <p>
     */
    @JsonProperty("time")
    public long getTime() {
        return time;
    }

    /**
     *
     * (Required)
     * <p>
     */
    @JsonProperty("time")
    public void setTime(long time) {
        this.time = time;
    }

    @JsonProperty("value")
    public double getValue() {
        return value;
    }

    @JsonProperty("value")
    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return new StringBuilder("{").append("time:").append(time).
                append(", value:").append(value).append("}").toString();
    }
}
