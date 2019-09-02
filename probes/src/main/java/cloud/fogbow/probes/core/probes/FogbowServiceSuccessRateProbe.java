package cloud.fogbow.probes.core.probes;

import cloud.fogbow.probes.core.Constants;
import cloud.fogbow.probes.core.fta.FtaConverter;
import cloud.fogbow.probes.core.fta.FtaSender;
import cloud.fogbow.probes.core.models.Observation;
import cloud.fogbow.probes.core.models.OrderState;
import cloud.fogbow.probes.core.models.Probe;
import cloud.fogbow.probes.core.models.ResourceType;
import java.util.ArrayList;
import javafx.util.Pair;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;

@Component
public class FogbowServiceSuccessRateProbe extends Probe {

    private static final String PROBE_LABEL = "service_success_rate";

    @PostConstruct
    public void FogbowServiceSuccessRateProbe() {
        this.lastTimestampAwake = new Timestamp(System.currentTimeMillis());
        this.probeId = Integer.valueOf(properties.getProperty(Constants.SERVICE_SUCCESS_RATE_PROBE_ID));
        this.firstTimeAwake = true;
        this.SLEEP_TIME = Integer.valueOf(properties.getProperty(Constants.SLEEP_TIME));
        this.FMA_ADDRESS = properties.getProperty(Constants.FMA_ADDRESS).trim();
    }

    public void run() {
        while(true) {
            Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
            lastTimestampAwake = currentTimestamp;
            Observation observation = makeObservation(lastTimestampAwake);
            FtaSender.sendObservation(FMA_ADDRESS, observation);
            sleep(SLEEP_TIME);
        }
    }

    private Observation makeObservation(Timestamp currentTimestamp){
        List<Pair<String, Float>> resourcesAvailability = new ArrayList<>();
        ResourceType resourceTypes[] = {ResourceType.COMPUTE, ResourceType.VOLUME, ResourceType.NETWORK};
        for(ResourceType r : resourceTypes){
            resourcesAvailability.add(getResourceAvailabilityValue(r));
        }
        Observation observation = FtaConverter
            .createObservation(PROBE_LABEL, resourcesAvailability, currentTimestamp);
        return observation;
    }

    private Pair<String, Float> getResourceAvailabilityValue(ResourceType type){
        Integer valueFailed = providerService.getAuditsFromResourceByState(OrderState.FAILED_ON_REQUEST, type,
            lastTimestampAwake, firstTimeAwake);
        Integer valueOpen = providerService.getAuditsFromResourceByState(OrderState.OPEN, type,
            lastTimestampAwake, firstTimeAwake);
        Float availabilityData = (float) (valueOpen / (valueFailed + valueOpen));
        Pair<String, Float> pair = new Pair<>(type.getValue(), availabilityData);
        return pair;
    }
}
