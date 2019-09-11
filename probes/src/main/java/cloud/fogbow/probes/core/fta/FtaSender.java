package cloud.fogbow.probes.core.fta;

import cloud.fogbow.probes.core.fta.http.HttpWrapper;
import cloud.fogbow.probes.core.models.Metric;
import java.util.ArrayList;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FtaSender {

    private static final Logger LOGGER = LogManager.getLogger(FtaSender.class);

    public static void sendObservation(String address, Metric metric) {
        try {
            LOGGER.info("Sending metric to [" + address + "]");
            StringEntity body = new StringEntity(metric.toJson().toString());
            HttpWrapper.doRequest(HttpPost.METHOD_NAME, address, new ArrayList<>(), body);
        } catch (Exception e) {
            LOGGER.error("Error while sending metric: " + e.getMessage());
        }
    }
}
