package cloud.fogbow.probes.core.probes;

import cloud.fogbow.probes.core.models.Observation;
import cloud.fogbow.probes.core.models.Probe;
import java.util.Arrays;
import cloud.fogbow.probes.core.utils.Pair;
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

    public void run() {
        while(true) {
            LOGGER.info("----> Starting Fogbow Service Latency Probe...");
            super.run();
        }
    }

    protected Observation makeObservation(Timestamp currentTimestamp){
        Long[] latencies = this.providerService.getLatencies(currentTimestamp, firstTimeAwake);
        List<Pair<String, Float>> values = toValue(latencies);
        Observation observation = new Observation(PROBE_LABEL, values, currentTimestamp);
        LOGGER.info("Made a observation with label [" + observation.getLabel() + "] at [" + currentTimestamp.toString() + "]");
        return observation;
    }

    private List<Pair<String, Float>> toValue(Long[] latencies){
        Pair<String, Float> computeLatency = new Pair<>(COMPUTE_JSON_KEY, (float) latencies[0]);
        Pair<String, Float> networkLatency = new Pair<>(NETWORK_JSON_KEY, (float) latencies[1]);
        Pair<String, Float> volumeLatency = new Pair<>(VOLUME_JSON_KEY, (float) latencies[2]);
        List<Pair<String, Float>> list = new ArrayList<>(
            Arrays.asList(computeLatency, networkLatency, volumeLatency));
        return list;
    }
}
