package cloud.fogbow.probes.core.utils;

import static cloud.fogbow.probes.core.utils.PropertiesUtil.loadProperties;

import cloud.fogbow.probes.FogbowProbesApplication;
import cloud.fogbow.probes.core.Constants;
import cloud.fogbow.probes.core.PropertiesHolder;
import cloud.fogbow.probes.core.models.Metric;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;
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

    public static Properties readProperties() {
        Properties properties = new Properties();
        String confFilePath = System.getProperty(PropertiesHolder.CONF_FILE_PROPERTY);

        try {
            if (Objects.isNull(confFilePath)) {
                confFilePath = "private/" + Constants.CONF_FILE;
                properties.load(FogbowProbesApplication.class.getClassLoader()
                    .getResourceAsStream(confFilePath));
            } else {
                properties = loadProperties(confFilePath);
            }
        } catch (Exception e) {
            System.exit(1);
        }
        return properties;
    }

    public static String metricsToString(List<Metric> metrics){
        String result = "\n";
        for(Metric m : metrics){
            result = result.concat(m.toString() + '\n');
        }
        return result;
    }
}
