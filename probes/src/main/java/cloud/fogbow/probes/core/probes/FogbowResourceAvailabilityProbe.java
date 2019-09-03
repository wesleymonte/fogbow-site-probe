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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;

@Component
public class FogbowResourceAvailabilityProbe extends Probe {

    private static final String PROBE_LABEL = "resource_availability_probe";
    private static final Logger LOGGER = LogManager.getLogger(FogbowResourceAvailabilityProbe.class);

    public void run() {
        while(true) {
            LOGGER.info("----> Starting Fogbow Resource Availability Probe...");
            super.run();
        }
    }

    protected Observation makeObservation(Timestamp currentTimestamp){
        List<Pair<String, Float>> resourcesAvailability = new ArrayList<>();
        ResourceType resourceTypes[] = {ResourceType.COMPUTE, ResourceType.VOLUME, ResourceType.NETWORK};
        for(ResourceType r : resourceTypes){
            resourcesAvailability.add(getResourceAvailabilityValue(r));
        }
        Observation observation = FtaConverter
            .createObservation(PROBE_LABEL, resourcesAvailability, currentTimestamp);
        LOGGER.info("Made a observation with label [" + observation.getLabel() + "] at [" + currentTimestamp.toString() + "]");
        return observation;
    }

    private Pair<String, Float> getResourceAvailabilityValue(ResourceType type){
        LOGGER.debug("Getting audits from resource of type [" + type.getValue() + "]");
        Integer valueFailedAfterSuccessful = providerService.getAuditsFromResourceByState(OrderState.FAILED_AFTER_SUCCESSFUL_REQUEST, type,
            lastTimestampAwake, firstTimeAwake);
        Integer valueFulfilled = providerService.getAuditsFromResourceByState(OrderState.FULFILLED, type,
            lastTimestampAwake, firstTimeAwake);
        Float availabilityData = calculateAvailabilityData(valueFailedAfterSuccessful, valueFulfilled);
        LOGGER.debug("Value of availability data [" + availabilityData + "]");
        Pair<String, Float> pair = new Pair<>(type.getValue(), availabilityData);
        return pair;
    }

    private Float calculateAvailabilityData(Integer valueFailedAfterSuccessful, Integer valueFulfilled){
        float result = 100;
        if(valueFulfilled != 0){
            result = 100 * (1 - valueFailedAfterSuccessful / valueFulfilled);
        }
        return result;
    }


}
