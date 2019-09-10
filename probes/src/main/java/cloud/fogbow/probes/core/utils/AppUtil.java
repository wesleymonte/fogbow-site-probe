package cloud.fogbow.probes.core.utils;

import cloud.fogbow.probes.core.models.Value;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import org.json.JSONArray;
import org.json.JSONObject;

public class AppUtil {

    public static void makeBodyField(JSONObject json, String key, String value) {
        if (value != null && !value.isEmpty()) {
            json.put(key, value);
        }
    }

    public static void makeBodyField(JSONObject json, String key, Long value) {
        if (value != null) {
            json.put(key, value);
        }
    }

    public static void makeBodyField(JSONObject json, String key,
        List<Value> values) {
        if (values != null && !values.isEmpty()) {
            JSONArray jsonArray = new JSONArray();
            makeBodyField(jsonArray, values);
            json.put(key, jsonArray);
        }
    }

    public static void makeBodyField(JSONArray json, List<Value> values) {
        if (values != null && !values.isEmpty()) {
            for (Value v : values) {
                JSONObject jsonPair = toJsonObject(v);
                json.put(jsonPair);
            }
        }
    }

    public static JSONObject toJsonObject(Value v) {
        JSONObject json = new JSONObject();
        if (v != null && !v.getDescription().trim().isEmpty() && !Objects.isNull(v.getMeasurement())) {
            json.put("description", v.getDescription());
            json.put("measurement", v.getMeasurement());
        }
        return json;
    }

    public static String timestampToDate(long timestamp) {
        Date date = new java.util.Date(timestamp * 1000L);
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT-3"));
        return sdf.format(date);
    }

    public static void sleep(int sleepTime) {
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
