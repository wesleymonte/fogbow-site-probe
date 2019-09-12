package cloud.fogbow.probes.core.utils;

import cloud.fogbow.probes.core.models.Observation;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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

    private static JSONObject toJson(Map<String, String> map){
        JSONObject jsonObject = new JSONObject();
        for(Entry<String, String> e : map.entrySet()){
            jsonObject.put(e.getKey(), e.getValue());
        }
        return jsonObject;
    }

    public static void makeBodyField(JSONObject json, String key, List<Observation> observations) {
        if (observations != null && !observations.isEmpty()) {
            JSONArray jsonArray = new JSONArray();
            makeBodyField(jsonArray, observations);
            json.put(key, jsonArray);
        }
    }

    public static void makeBodyField(JSONArray json, List<Observation> observations) {
        if (observations != null && !observations.isEmpty()) {
            for (Observation observation : observations) {
                json.put(observation.toJson());
            }
        }
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
