package cloud.fogbow.probes.core.probes;

import cloud.fogbow.probes.core.Constants;
import cloud.fogbow.probes.core.fta.FtaConverter;
import cloud.fogbow.probes.core.models.Observation;
import cloud.fogbow.probes.core.models.OrderState;
import cloud.fogbow.probes.core.models.Probe;
import cloud.fogbow.probes.core.models.ResourceType;
import cloud.fogbow.probes.core.utils.Pair;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * FogbowResourceAvailabilityProbe is responsible for measuring the availability level of Fogbow
 * resources. Resource availability is calculated by the ratio between the number of orders in
 * {@link OrderState#FULFILLED} and in {@link OrderState#FAILED_AFTER_SUCCESSFUL_REQUEST}. This
 * metric measures the level of failure to request a resource after your {@link
 * cloud.fogbow.probes.core.models.Order} is {@link OrderState#OPEN}.
 */
@Component
public class FogbowResourceAvailabilityProbe extends Probe {

    private static final String PROBE_NAME = "resource_availability";
    private static final String HELP = "Metric measures the level of failure to request a resource after your Order is Open.";
    private static final Logger LOGGER = LogManager
        .getLogger(FogbowResourceAvailabilityProbe.class);

    @PostConstruct
    public void FogbowResourceAvailabilityProbe() {
        this.PROBE_ID = Integer
            .valueOf(properties.getProperty(Constants.RESOURCE_AVAILABILITY_PROBE_ID));
    }

    public void run() {
        while (true) {
            LOGGER.info("----> Starting Fogbow Resource Availability Probe...");
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
            .createObservation(PROBE_NAME, resourcesAvailability, currentTimestamp, HELP);
        LOGGER.info(
            "Made a observation with name [" + observation.getName() + "] at [" + currentTimestamp
                .toString() + "]");
        return observation;
    }

    private Pair<String, Float> getResourceAvailabilityValue(ResourceType type) {
        LOGGER.debug("Getting audits from resource of type [" + type.getValue() + "]");
        Integer valueFailedAfterSuccessful = providerService
            .getAuditsFromResourceByState(OrderState.FAILED_AFTER_SUCCESSFUL_REQUEST, type,
                lastTimestampAwake, firstTimeAwake);
        Integer valueFulfilled = providerService
            .getAuditsFromResourceByState(OrderState.FULFILLED, type, lastTimestampAwake,
                firstTimeAwake);
        Float availabilityData = calculateAvailabilityData(valueFailedAfterSuccessful,
            valueFulfilled);
        LOGGER.debug("Value of availability data [" + availabilityData + "]");
        Pair<String, Float> pair = new Pair<>(type.getValue(), availabilityData);
        return pair;
    }

    /**
     * Calculates the percentage of Orders that did not fail after the request.
     *
     * @param valueFailedAfterSuccessful Quantity of orders in {@link OrderState#FAILED_AFTER_SUCCESSFUL_REQUEST}
     * @param valueFulfilled Quantity of orders in {@link OrderState#FULFILLED}
     * @return float with the resulting percentage
     */
    private Float calculateAvailabilityData(Integer valueFailedAfterSuccessful,
        Integer valueFulfilled) {
        float result = 100;
        if (valueFulfilled != 0) {
            result = 100 * (1 - (float) valueFailedAfterSuccessful / (float) valueFulfilled);
        }
        return result;
    }


}
