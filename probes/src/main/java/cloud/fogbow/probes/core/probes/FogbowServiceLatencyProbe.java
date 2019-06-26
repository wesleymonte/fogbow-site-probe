package cloud.fogbow.probes.core.probes;

import cloud.fogbow.probes.core.Constants;
import cloud.fogbow.probes.core.models.Probe;
import cloud.fogbow.probes.core.utils.PropertiesUtil;
import javafx.util.Pair;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Component
public class FogbowServiceLatencyProbe extends Probe {

    private int SLEEP_TIME;

    public FogbowServiceLatencyProbe() throws Exception{
        this.lastTimestampAwake = new Timestamp(System.currentTimeMillis());

        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath() + "private/";
        this.properties = new PropertiesUtil().readProperties(path + Constants.CONF_FILE);

        this.probeId = Integer.valueOf(properties.getProperty(Constants.SERVICE_LATENCY_PROBE_ID));
        this.firstTimeAwake = true;
        this.SLEEP_TIME = Integer.valueOf(properties.getProperty(Constants.SLEEP_TIME));
    }

    public void run() {
        setup();

        while(true) {
            List<Pair<Number, Timestamp>>[] latencies = this.providerService.getLatencies(lastTimestampAwake, firstTimeAwake);

            List<List<Pair<Number, Timestamp>>> latenciesWrapper = new ArrayList<>();
            latenciesWrapper.add(latencies[0]);

            this.firstTimeAwake = false;
            this.lastTimestampAwake = new Timestamp(System.currentTimeMillis());

            if(!latencies[0].isEmpty()) {
                this.resourceId = Integer.valueOf(properties.getProperty(Constants.COMPUTE_RESOURCE_ID));
                sendMessage(latenciesWrapper);
            }

            latenciesWrapper.clear();
            latenciesWrapper.add(latencies[1]);

            if(!latencies[1].isEmpty()) {
                this.resourceId = Integer.valueOf(properties.getProperty(Constants.VOLUME_RESOURCE_ID));
                sendMessage(latenciesWrapper);
            }

            latenciesWrapper.clear();
            latenciesWrapper.add(latencies[2]);

            if(!latencies[2].isEmpty()) {
                this.resourceId = Integer.valueOf(properties.getProperty(Constants.NETWORK_RESOURCE_ID));
                sendMessage(latenciesWrapper);
            }

            sleep(SLEEP_TIME);
        }
    }
}
