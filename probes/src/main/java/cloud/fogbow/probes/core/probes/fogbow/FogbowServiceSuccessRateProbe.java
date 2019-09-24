package cloud.fogbow.probes.core.probes.fogbow;

import cloud.fogbow.probes.core.models.Metric;
import cloud.fogbow.probes.core.models.OrderState;
import cloud.fogbow.probes.core.models.ResourceType;
import cloud.fogbow.probes.core.utils.Pair;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FogbowServiceSuccessRateProbe is responsible for measuring the success rate in requesting a
 * resource. The success rate is measured by the relationship between the number of orders that
 * failed after requests ({@link OrderState#FAILED_AFTER_SUCCESSFUL_REQUEST}) and the number of
 * orders opened ({@link OrderState#OPEN}).
 */
public class FogbowServiceSuccessRateProbe extends FogbowProbe {

    private static final Logger LOGGER = LogManager.getLogger(FogbowServiceSuccessRateProbe.class);
    private static final String HELP = "The success rate in requesting a resource.";
    private static final String METRIC_NAME = "success_rate";
    private static final String METRIC_VALUE_TYPE = "resource";

    public FogbowServiceSuccessRateProbe(String ftaAddress, String targetHostAddress) {
        super(targetHostAddress, ftaAddress, HELP, METRIC_NAME, METRIC_VALUE_TYPE);
    }

    protected List<Metric> getMetrics(Timestamp currentTimestamp) {
        List<Pair<String, Float>> resourcesAvailability = new ArrayList<>();
        ResourceType[] resourceTypes = {ResourceType.COMPUTE, ResourceType.VOLUME,
            ResourceType.NETWORK};
        for (ResourceType r : resourceTypes) {
            resourcesAvailability.add(getResourceAvailabilityValue(r));
        }
        List<Metric> metrics = parseValuesToMetrics(resourcesAvailability, currentTimestamp);
        LOGGER.info("Made a metric with name at [" + currentTimestamp.toString() + "]");
        return metrics;
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
        LOGGER.debug("Metric of availability data [" + availabilityData + "]");
        Pair<String, Float> pair = new Pair<>(type.getValue(), availabilityData);
        return pair;
    }

    /**
     * Calculates the percentage of Orders that did not fail on the request.
     *
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
