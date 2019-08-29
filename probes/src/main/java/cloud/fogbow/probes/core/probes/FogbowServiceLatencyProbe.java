package cloud.fogbow.probes.core.probes;

import cloud.fogbow.probes.core.Constants;
import cloud.fogbow.probes.core.models.Observation;
import cloud.fogbow.probes.core.models.Probe;
import javafx.util.Pair;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Component
public class FogbowServiceLatencyProbe extends Probe {

    private int SLEEP_TIME;
    private static final String PROBE_LABEL = "service_latency_probe";

    @PostConstruct
    public void FogbowServiceLatencyProbe() {
        this.lastTimestampAwake = new Timestamp(System.currentTimeMillis());
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

            Integer resourceId;
            if(!latencies[0].isEmpty()) {
                resourceId = Integer.valueOf(properties.getProperty(Constants.COMPUTE_RESOURCE_ID));
                sendMessage(resourceId, latenciesWrapper);
            }

            latenciesWrapper.clear();
            latenciesWrapper.add(latencies[1]);

            if(!latencies[1].isEmpty()) {
                resourceId = Integer.valueOf(properties.getProperty(Constants.VOLUME_RESOURCE_ID));
                sendMessage(resourceId, latenciesWrapper);
            }

            latenciesWrapper.clear();
            latenciesWrapper.add(latencies[2]);

            if(!latencies[2].isEmpty()) {
                resourceId = Integer.valueOf(properties.getProperty(Constants.NETWORK_RESOURCE_ID));
                sendMessage(resourceId, latenciesWrapper);
            }

            sleep(SLEEP_TIME);
        }
    }

    public Observation getObservation(){
        List<Pair<Number, Timestamp>>[] latencies = this.providerService.getLatencies(lastTimestampAwake, firstTimeAwake);
        List<Pair<String, Float>> values = toValue(latencies);
        Observation observation = new Observation(PROBE_LABEL, values, lastTimestampAwake);
        return observation;
    }

    private List<Pair<String, Float>> toValue(List<Pair<Number, Timestamp>>[] latencies){
        List<Pair<String, Float>> list = new ArrayList<>();
        String key;
        for(int i = 0; i < 3; i++){
            if(i == 0) key = "COMPUTE";
            else if(i == 1) key = "VOLUME";
            else key = "NETWORK";
            Float value = latencies[i].get(0).getKey().floatValue();
            Pair<String, Float> p = new Pair<>(key, value);
            list.add(p);
        }
        return list;
    }
}
