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
public class FogbowResourceAvailabilityProbe extends Probe {

    private static final String PROBE_LABEL = "resource_availability_probe";

    @PostConstruct
    public void FogbowResourceAvailabilityProbe(){
        this.lastTimestampAwake = new Timestamp(System.currentTimeMillis());
        this.probeId = Integer.valueOf(properties.getProperty(Constants.RESOURCE_AVAILABILITY_PROBE_ID));
        this.firstTimeAwake = true;
        this.SLEEP_TIME = Integer.valueOf(properties.getProperty(Constants.SLEEP_TIME));
        this.FMA_ADDRESS = properties.getProperty(Constants.FMA_ADDRESS).trim();
    }

    public void run() {
        while(true) {
            Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
            Observation observation = makeObservation(currentTimestamp);
            FtaSender.sendObservation(FMA_ADDRESS, observation);
            lastTimestampAwake = currentTimestamp;
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
        Integer valueFailedAfterSuccessful = providerService.getAuditsFromResourceByState(OrderState.FAILED_AFTER_SUCCESSFUL_REQUEST, type,
            lastTimestampAwake, firstTimeAwake);
        Integer valueFulfilled = providerService.getAuditsFromResourceByState(OrderState.FULFILLED, type,
            lastTimestampAwake, firstTimeAwake);
        Float availabilityData = (float) (valueFulfilled / (valueFailedAfterSuccessful + valueFulfilled));
        Pair<String, Float> pair = new Pair<>(type.getValue(), availabilityData);
        return pair;

    }


}
