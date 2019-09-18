package cloud.fogbow.probes.core.probes.docker;

import cloud.fogbow.probes.core.utils.http.HttpWrapper;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONObject;

public class DockerRequestHelper {

    private static String address = "http://localhost:5555";
    private static final String CONTAINER_STATS_ENDPOINT = "%s/containers/%s/stats?stream=false";
    private static final String LIST_CONTAINERS_ENDPOINT = "%s/containers/json";

    public JSONObject getContainerStats(String containerId){
        final String url = String.format(CONTAINER_STATS_ENDPOINT, address, containerId);
        JSONObject json = new JSONObject();
        try {
            String result = HttpWrapper.doRequest(HttpGet.METHOD_NAME, url, null);
            json = new JSONObject(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    public List<String> listContainersName(){
        List<String> list = new ArrayList<>();
        JSONArray json = listContainers();
        for(int i = 0; i < json.length(); i++){
            String name = getContainerName(json.getJSONObject(0));
            list.add(name);
        }
        return list;
    }

    private JSONArray listContainers(){
        final String url = String.format(LIST_CONTAINERS_ENDPOINT, address);
        String jsonStr = null;
        try {
            jsonStr = HttpWrapper.doRequest(HttpGet.METHOD_NAME, url, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONArray json = new JSONArray(jsonStr);
        return json;
    }

    private String getContainerName(JSONObject jsonObject){
        JSONArray names = jsonObject.getJSONArray("Names");
        String name = names.getString(0);
        return name;
    }
}
