package cloud.fogbow.probes.core.fta;

import cloud.fogbow.probes.core.fta.http.HttpWrapper;
import cloud.fogbow.probes.core.models.Observation;
import cloud.fogbow.probes.core.utils.AppUtil;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class FtaSender {

    private static final String METRIC_LABEL_JSON_KEY = "label";
    private static final String VALUES_JSON_KEY = "values";
    private static final String TIMESTAMP_JSON_KEY = "timestamp";
    private static final Logger LOGGER = LogManager.getLogger(FtaSender.class);

    public static void sendObservation(String address, Observation observation) {
        try {
            LOGGER.info("Sending observation to [" + address + "]");
            StringEntity body = toJson(observation);
            LOGGER.debug("Observation json body: " + EntityUtils.toString(body));
            HttpWrapper.doRequest(HttpPost.METHOD_NAME, address, new ArrayList<>(), body);
        } catch (Exception e) {
            LOGGER.error("Error while sending observation: " + e.getMessage());
        }
    }

    private static StringEntity toJson(Observation observation) throws UnsupportedEncodingException {
        JSONObject jsonObject = new JSONObject();
        AppUtil.makeBodyField(jsonObject, METRIC_LABEL_JSON_KEY, observation.getLabel());
        AppUtil.makeBodyField(jsonObject, VALUES_JSON_KEY, observation.getValues());
        AppUtil.makeBodyField(jsonObject, TIMESTAMP_JSON_KEY, observation.getTimestamp().getTime());
        return new StringEntity(jsonObject.toString());
    }
}
