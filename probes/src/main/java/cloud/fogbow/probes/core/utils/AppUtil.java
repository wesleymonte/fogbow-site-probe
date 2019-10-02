package cloud.fogbow.probes.core.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
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

    public static void makeBodyField(JSONObject json, String key, Float value) {
        if (value != null) {
            json.put(key, value);
        }
    }

    public static void makeBodyField(JSONObject json, String key, Map<String, String> map) {
        if (map != null && !map.isEmpty()) {
            json.put(key, toJson(map));
        }
    }

    private static JSONObject toJson(Map<String, String> map) {
        JSONObject jsonObject = new JSONObject();
        for (Entry<String, String> e : map.entrySet()) {
            jsonObject.put(e.getKey(), e.getValue());
        }
        return jsonObject;
    }

    public static String timestampToDate(long timestamp) {
        Date date = new java.util.Date(timestamp * 1000L);
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT-3"));
        return sdf.format(date);
    }

    public static Float percent(Integer dividend, Integer divisor) {
        float result = 100;
        if (divisor != 0) {
            result = 100 * (1 - (float) dividend / (float) divisor);
        }
        return result;
    }
}
