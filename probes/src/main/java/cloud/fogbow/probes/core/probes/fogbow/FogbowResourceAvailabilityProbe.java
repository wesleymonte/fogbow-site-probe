package cloud.fogbow.probes.core.probes.fogbow;

import cloud.fogbow.probes.core.models.Metric;
import cloud.fogbow.probes.core.models.OrderState;
import cloud.fogbow.probes.core.models.ResourceType;
import cloud.fogbow.probes.core.utils.Pair;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FogbowResourceAvailabilityProbe is responsible for measuring the availability level of Fogbow
 * resources. Resource availability is calculated by the ratio between the number of orders in
 * {@link OrderState#FULFILLED} and in {@link OrderState#FAILED_AFTER_SUCCESSFUL_REQUEST}. This
 * metric measures the level of failure to request a resource after your {@link
 * cloud.fogbow.probes.core.models.Order} is {@link OrderState#OPEN}.
 */

public class FogbowResourceAvailabilityProbe extends FogbowProbe {

    private static final Logger LOGGER = LogManager
        .getLogger(FogbowResourceAvailabilityProbe.class);
    private static final String HELP = "Measures the level of failure to request a resource after the Order is open.";
    private static final String METRIC_NAME = "availability";
    private static final String RESOURCE_LABEL = "resource";
    private static final ResourceType[] resourceTypes = {ResourceType.COMPUTE, ResourceType.VOLUME,
        ResourceType.NETWORK};

    public FogbowResourceAvailabilityProbe(String targetLabel, String probeTarget,
        String ftaAddress) {
        super(targetLabel, probeTarget, ftaAddress, HELP, METRIC_NAME);
    }

    protected List<Metric> getMetrics(Timestamp currentTimestamp) {
        List<Pair<String, Float>> resourcesAvailability = new ArrayList<>();
        for (ResourceType r : resourceTypes) {
            try {
                resourcesAvailability.add(getResourceAvailabilityValue(r));
            } catch (Exception e){
                LOGGER.error(r.getValue() + ": " + e.getMessage());
            }
        }
        List<Metric> metrics = parseValuesToMetrics(resourcesAvailability, currentTimestamp);
        LOGGER.info("Made as metric at [" + currentTimestamp.toString() + "]");
        return metrics;
    }

    protected void populateMetadata(Map<String, String> metadata, Pair<String, Float> p) {
        metadata.put(RESOURCE_LABEL, p.getKey().toLowerCase());
    }

    private Pair<String, Float> getResourceAvailabilityValue(ResourceType type) throws Exception {
        LOGGER.debug("Getting audits from resource of type [" + type.getValue() + "]");
        Integer valueFailedAfterSuccessful = providerService
            .getAuditsFromResourceByState(OrderState.FAILED_AFTER_SUCCESSFUL_REQUEST, type,
                lastTimestampAwake);
        Integer valueFulfilled = providerService
            .getAuditsFromResourceByState(OrderState.FULFILLED, type, lastTimestampAwake);
        if(valueFailedAfterSuccessful == 0 && valueFulfilled == 0){
            throw new Exception("Not found resource data to calculate.");
        }
        Float availabilityData = calculateAvailabilityData(valueFailedAfterSuccessful,
            valueFulfilled);
        LOGGER.debug("Observation of availability data [" + availabilityData + "]");
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
