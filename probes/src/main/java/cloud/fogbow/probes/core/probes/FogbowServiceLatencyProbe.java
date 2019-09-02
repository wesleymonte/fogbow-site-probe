package cloud.fogbow.probes.core.probes;

import cloud.fogbow.probes.core.Constants;
import cloud.fogbow.probes.core.fta.FtaSender;
import cloud.fogbow.probes.core.models.Observation;
import cloud.fogbow.probes.core.models.Probe;
import javafx.util.Pair;
import javax.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Component
public class FogbowServiceLatencyProbe extends Probe {

    private static final String PROBE_LABEL = "service_latency_probe";
    private static final Logger LOGGER = LogManager.getLogger(FogbowServiceLatencyProbe.class);
    private static final String COMPUTE_JSON_KEY = "COMPUTE";
    private static final String NETWORK_JSON_KEY = "NETWORK";
    private static final String VOLUME_JSON_KEY = "VOLUME";

    @PostConstruct
    public void FogbowServiceLatencyProbe() {
        this.lastTimestampAwake = new Timestamp(System.currentTimeMillis());
        this.probeId = Integer.valueOf(properties.getProperty(Constants.SERVICE_LATENCY_PROBE_ID));
        this.firstTimeAwake = true;
        this.SLEEP_TIME = Integer.valueOf(properties.getProperty(Constants.SLEEP_TIME));
        this.FTA_ADDRESS = properties.getProperty(Constants.FMA_ADDRESS).trim();
    }

    public void run() {
        while(true) {
            LOGGER.info("----> Starting Fogbow Service Latency Probe...");
            Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
            Observation observation = makeObservation(currentTimestamp);
            LOGGER.info("Probe[" + this.probeId + "] made a observation at [" + observation.getTimestamp().toString() + "]");
            FtaSender.sendObservation(FTA_ADDRESS, observation);
            lastTimestampAwake = currentTimestamp;
            sleep(SLEEP_TIME);
        }
    }

    public Observation makeObservation(Timestamp currentTimestamp){
        List<Pair<Number, Timestamp>>[] latencies = this.providerService.getLatencies(currentTimestamp, firstTimeAwake);
        List<Pair<String, Float>> values = toValue(latencies);
        Observation observation = new Observation(PROBE_LABEL, values, currentTimestamp);
        LOGGER.info("Made a observation with label [" + observation.getLabel() + "] at [" + currentTimestamp.toString() + "]");
        return observation;
    }

    private List<Pair<String, Float>> toValue(List<Pair<Number, Timestamp>>[] latencies){
        List<Pair<String, Float>> list = new ArrayList<>();
        String key;
        for(int i = 0; i < 3; i++){
            if(i == 0) key = COMPUTE_JSON_KEY;
            else if(i == 1) key = VOLUME_JSON_KEY;
            else key = NETWORK_JSON_KEY;
            Float value = latencies[i].get(0).getKey().floatValue();
            LOGGER.info("Got latency of [" + key + "] with value [" + value + "]");
            Pair<String, Float> p = new Pair<>(key, value);
            list.add(p);
        }
        return list;
    }
}
