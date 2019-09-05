package cloud.fogbow.probes.core.probes;

import cloud.fogbow.probes.core.fta.FtaConverter;
import cloud.fogbow.probes.core.models.Observation;
import cloud.fogbow.probes.core.models.OrderState;
import cloud.fogbow.probes.core.models.Probe;
import cloud.fogbow.probes.core.models.ResourceType;
import cloud.fogbow.probes.core.utils.Pair;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * FogbowServiceSuccessRateProbe is responsible for measuring the success rate in requesting a resource. The success rate is
 * measured by the relationship between the number of orders that failed after requests ({@link OrderState#FAILED_AFTER_SUCCESSFUL_REQUEST}) and the
 * number of orders opened ({@link OrderState#OPEN}).
 */
@Component
public class FogbowServiceSuccessRateProbe extends Probe {

    private static final String PROBE_LABEL = "service_success_rate";
    private static final Logger LOGGER = LogManager.getLogger(FogbowServiceSuccessRateProbe.class);

    public void run() {
        while (true) {
            LOGGER.info("----> Starting Fogbow Service Success Rate Probe");
            super.run();
        }
    }

    protected Observation makeObservation(Timestamp currentTimestamp) {
        List<Pair<String, Float>> resourcesAvailability = new ArrayList<>();
        ResourceType resourceTypes[] = {ResourceType.COMPUTE, ResourceType.VOLUME,
            ResourceType.NETWORK};
        for (ResourceType r : resourceTypes) {
            resourcesAvailability.add(getResourceAvailabilityValue(r));
        }
        Observation observation = FtaConverter
            .createObservation(PROBE_LABEL, resourcesAvailability, currentTimestamp);
        LOGGER.info(
            "Made a observation with label [" + observation.getLabel() + "] at [" + currentTimestamp
                .toString() + "]");
        return observation;
    }

    private Pair<String, Float> getResourceAvailabilityValue(ResourceType type) {
        LOGGER.debug("Getting audits from resource of type [" + type.getValue() + "]");
        Integer valueFailed = providerService
            .getAuditsFromResourceByState(OrderState.FAILED_ON_REQUEST, type, lastTimestampAwake,
                firstTimeAwake);
        Integer valueOpen = providerService
            .getAuditsFromResourceByState(OrderState.OPEN, type, lastTimestampAwake,
                firstTimeAwake);
        Float availabilityData = calculateAvailabilityData(valueFailed, valueOpen);
        LOGGER.debug("Value of availability data [" + availabilityData + "]");
        Pair<String, Float> pair = new Pair<>(type.getValue(), availabilityData);
        return pair;
    }

    /**
     * Calculates the percentage of Orders that did not fail on the request.
     * @param valueFailed Quantity of orders in {@link OrderState#FAILED_ON_REQUEST}
     * @param valueOpen Quantity of orders in {@link OrderState#OPEN}
     * @return float with the resulting percentage
     */
    private Float calculateAvailabilityData(Integer valueFailed, Integer valueOpen) {
        float result = 100;
        if (valueOpen != 0) {
            result = 100 * (1 - (float) valueFailed / (float) valueOpen);
        }
        return result;
    }
}
