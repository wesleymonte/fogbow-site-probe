package cloud.fogbow.probes.core.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javafx.util.Pair;
import org.json.JSONArray;
import org.json.JSONObject;

public class AppUtil {

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

    public static String timestampToDate(long timestamp) {
        Date date = new java.util.Date(timestamp * 1000L);
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT-3"));
        return sdf.format(date);
    }
}
