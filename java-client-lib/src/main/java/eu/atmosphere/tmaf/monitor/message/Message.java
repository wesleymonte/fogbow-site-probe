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
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * tma-m_schema_0_3::Message
 * <p>
 * <p>
 * @author Nuno Antunes <nmsa@dei.uc.pt>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "probeId",
    "resourceId",
    "messageId",
    "sentTime",
    "data"
})
public class Message implements Serializable {

    private final static long serialVersionUID = -3266346137732950545L;
    /**
     *
     * (Required)
     * <p>
     */
    @JsonProperty("probeId")
    private long probeId = -1L;
    /**
     *
     * (Required)
     * <p>
     */
    @JsonProperty("resourceId")
    private long resourceId = -1L;
    /**
     *
     * (Required)
     * <p>
     */
    @JsonProperty("messageId")
    private long messageId = -1L;
    /**
     *
     * (Required)
     * <p>
     */
    @JsonProperty("sentTime")
    private long sentTime = -1L;
    /**
     *
     * <p>
     */
    @JsonProperty("data")
    private final List<Data> data = new ArrayList<Data>();

    /**
     * No args constructor for use in serialization
     */
    public Message() {
    }

    /**
     *
     * @param sentTime
     * @param resourceId
     * @param probeId
     * @param data
     * @param messageId
     */
    public Message(long probeId, long resourceId, long messageId, long sentTime, List<Data> data) {
        super();
        this.probeId = probeId;
        this.resourceId = resourceId;
        this.messageId = messageId;
        this.sentTime = sentTime;
        this.data.addAll(data);
    }

    /**
     *
     */
    @JsonProperty("probeId")
    public long getProbeId() {
        return probeId;
    }

    /**
     *
     */
    @JsonProperty("probeId")
    public void setProbeId(long probeId) {
        this.probeId = probeId;
    }

    /**
     *
     */
    @JsonProperty("resourceId")
    public long getResourceId() {
        return resourceId;
    }

    /**
     *
     */
    @JsonProperty("resourceId")
    public void setResourceId(long resourceId) {
        this.resourceId = resourceId;
    }

    /**
     *
     */
    @JsonProperty("messageId")
    public long getMessageId() {
        return messageId;
    }

    /**
     *
     */
    @JsonProperty("messageId")
    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    /**
     *
     */
    @JsonProperty("sentTime")
    public long getSentTime() {
        return sentTime;
    }

    /**
     *
     */
    @JsonProperty("sentTime")
    public void setSentTime(long sentTime) {
        this.sentTime = sentTime;
    }

    /**
     *
     */
    @JsonProperty("data")
    public List<Data> getData() {
        return data;
    }

    /**
     *
     */
    @JsonProperty("data")
    public void setData(List<Data> data) {
        throw new UnsupportedOperationException("should not be used");
//        this.data = data;
    }

    public boolean addData(Data datum) {
        return this.data.add(datum);
    }

    public boolean addData(Data.Type type, int descriptionId, int time, double value) {
        return this.data.add(new Data(type, descriptionId, new Observation(time, value)));
    }

    @Override
    public String toString() {
        return new StringBuilder("MonitorMessage{").append("probeId:").append(probeId)
                .append(", resourceId:").append(resourceId)
                .append(", messageId:").append(messageId)
                .append(", sentTime:").append(sentTime)
                .append(", data:").append(data).append("}").toString();
    }
}
