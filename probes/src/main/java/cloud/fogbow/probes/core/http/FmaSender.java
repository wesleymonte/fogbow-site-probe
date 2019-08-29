package cloud.fogbow.probes.core.http;

import cloud.fogbow.probes.core.models.Observation;
import cloud.fogbow.probes.core.utils.AppUtil;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.log4j.Logger;
import org.json.JSONObject;

public class FmaSender {

    private static final String FMA_OBS_ENDPOINT = "/observation";
    private static final String METRIC_LABEL_JSON_KEY = "metric_label";
    private static final String VALUES_JSON_KEY = "values";
    private static final String TIMESTAMP_JSON_KEY = "timestamp";
    private static final Logger LOGGER = Logger.getLogger(FmaSender.class);

    public void sendObservation(Observation observation) {
        try {
            StringEntity body = toJson(observation);
            HttpWrapper.doRequest(HttpPost.METHOD_NAME, FMA_OBS_ENDPOINT, new ArrayList<>(), body);
        } catch (Exception e) {
            LOGGER.error("Error while sending observation: " + e.getMessage(), e);
        }
    }

    public StringEntity toJson(Observation observation) throws UnsupportedEncodingException {
        JSONObject jsonObject = new JSONObject();
        AppUtil.makeBodyField(jsonObject, METRIC_LABEL_JSON_KEY, observation.getLabel());
        AppUtil.makeBodyField(jsonObject, VALUES_JSON_KEY, observation.getValues());
        AppUtil.makeBodyField(jsonObject, TIMESTAMP_JSON_KEY, observation.getTimestamp());
        return new StringEntity(jsonObject.toString());
    }
}
