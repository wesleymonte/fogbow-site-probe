package cloud.fogbow.probes.core.probes;

import cloud.fogbow.probes.core.Constants;
import cloud.fogbow.probes.core.http.FmaConverter;
import cloud.fogbow.probes.core.models.FogbowDataProbe;
import cloud.fogbow.probes.core.models.Observation;
import cloud.fogbow.probes.core.models.OrderState;
import cloud.fogbow.probes.core.models.ResourceType;
import java.util.ArrayList;
import javafx.util.Pair;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;

@Component
public class FogbowResourceAvailabilityProbe extends FogbowDataProbe {

    private static final String PROBE_LABEL = "resource_availability_probe";

    @PostConstruct
    public void FogbowResourceAvailabilityProbe(){
        this.lastTimestampAwake = new Timestamp(System.currentTimeMillis());
        this.probeId = Integer.valueOf(properties.getProperty(Constants.RESOURCE_AVAILABILITY_PROBE_ID));
        this.firstTimeAwake = true;
        this.SLEEP_TIME = Integer.valueOf(properties.getProperty(Constants.SLEEP_TIME));
    }

    public void run() {
        setup();

        while(true) {
            Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

            List<List<Pair<Number, Timestamp>>> computeData = getResourceAvailabilityData(ResourceType.COMPUTE, currentTimestamp);
            List<List<Pair<Number, Timestamp>>> volumeData = getResourceAvailabilityData(ResourceType.VOLUME, currentTimestamp);
            List<List<Pair<Number, Timestamp>>> networkData = getResourceAvailabilityData(ResourceType.NETWORK, currentTimestamp);

            lastTimestampAwake = currentTimestamp;
            sendResourceDataMessages(computeData, volumeData, networkData);
        }
    }

    private List<List<Pair<Number, Timestamp>>> getResourceAvailabilityData(ResourceType type, Timestamp currentTimestamp) {
        List<List<Pair<Number, Timestamp>>> results;
        OrderState[] states = {OrderState.FAILED_AFTER_SUCCESSFUL_REQUEST, OrderState.FULFILLED};
        results = super.getData(states, type, currentTimestamp);
        return results;
    }

    private Observation makeObservation(Timestamp currentTimestamp){
        List<Pair<String, Float>> resourcesAvailability = new ArrayList<>();
        ResourceType resourceTypes[] = {ResourceType.COMPUTE, ResourceType.VOLUME, ResourceType.NETWORK};
        for(ResourceType r : resourceTypes){
            resourcesAvailability.add(getResourceAvailabilityValue(r, currentTimestamp, firstTimeAwake));
        }
        Observation observation = FmaConverter.createObservation(PROBE_LABEL, resourcesAvailability, currentTimestamp);
        return observation;
    }

    private Pair<String, Float> getResourceAvailabilityValue(ResourceType type, Timestamp lastTimestampAwake, boolean firstTimeAwake){
        Integer valueFailedAfterSuccessful = providerService.getAuditsFromResourceByState(OrderState.FAILED_AFTER_SUCCESSFUL_REQUEST, type,
            lastTimestampAwake, firstTimeAwake);
        Integer valueFulfilled = providerService.getAuditsFromResourceByState(OrderState.FULFILLED, type,
            lastTimestampAwake, firstTimeAwake);
        Float availabilityData = (float) (valueFulfilled / (valueFailedAfterSuccessful + valueFulfilled));
        Pair<String, Float> pair = new Pair<>(type.getValue(), availabilityData);
        return pair;

    }


}
