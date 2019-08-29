package cloud.fogbow.probes.core.http;

import cloud.fogbow.probes.core.models.Observation;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javafx.util.Pair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONObject;

public class FmaSender {

    private static final String FMA_OBS_ENDPOINT = "/observation";

    public static void makeBodyField(JSONObject json, String key, String value) {
        if (value != null && !value.isEmpty()) {
            json.put(key, value);
        }
    }

    public static JSONObject toJsonObject(Pair<String, Float> pair) {
        JSONObject json = new JSONObject();
        if (pair != null && !pair.getKey().trim().isEmpty() && !Objects.isNull(pair.getValue())) {
            json.put(pair.getKey(), pair.getValue());
        }
        return json;
    }

    public static void makeBodyField(JSONArray json, List<Pair<String, Float>> values) {
        if (values != null && !values.isEmpty()) {
            for (Pair<String, Float> p : values) {
                JSONObject jsonPair = toJsonObject(p);
                json.put(jsonPair);
            }
        }
    }

    public static void makeBodyField(JSONObject json, String key,
        List<Pair<String, Float>> values) {
        if (values != null && !values.isEmpty()) {
            JSONArray jsonArray = new JSONArray();
            makeBodyField(jsonArray, values);
            json.put(key, jsonArray);
        }
    }

    public static void makeBodyField(JSONObject json, String key, Timestamp timestamp) {
        if (timestamp != null) {
            json.put(key, timestamp);
        }
    }

    public void sendObservation(Observation observation) {
        try {
            StringEntity body = toJson(observation);
            HttpWrapper.doRequest(HttpPost.METHOD_NAME, FMA_OBS_ENDPOINT, new ArrayList<>(), body);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public StringEntity toJson(Observation observation) throws UnsupportedEncodingException {
        JSONObject jsonObject = new JSONObject();
        makeBodyField(jsonObject, "metric_label", observation.getLabel());
        makeBodyField(jsonObject, "values", observation.getValues());
        makeBodyField(jsonObject, "timestamp", observation.getTimestamp());
        return new StringEntity(jsonObject.toString());
    }
}
