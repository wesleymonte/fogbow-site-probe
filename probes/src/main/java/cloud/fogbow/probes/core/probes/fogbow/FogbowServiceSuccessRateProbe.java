package cloud.fogbow.probes.core.probes.fogbow;

import cloud.fogbow.probes.core.models.Metric;
import cloud.fogbow.probes.core.models.OrderState;
import cloud.fogbow.probes.core.models.ResourceType;
import cloud.fogbow.probes.core.probes.exception.OrdersStateChangeNotFoundException;
import cloud.fogbow.probes.core.utils.AppUtil;
import cloud.fogbow.probes.core.utils.Pair;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private static final String RESOURCE_LABEL = "resource";

    public FogbowServiceSuccessRateProbe() {
        super(HELP, METRIC_NAME);
    }

    public List<Metric> getMetrics(Timestamp currentTimestamp) {
        List<Pair<String, Float>> resourcesAvailability = new ArrayList<>();
        ResourceType[] resourceTypes = {ResourceType.COMPUTE, ResourceType.VOLUME,
            ResourceType.NETWORK};
        for (ResourceType r : resourceTypes) {
            try {
                resourcesAvailability.add(getResourceAvailabilityValue(r));
            } catch (Exception e){
                LOGGER.error(r.getValue() + ": " + e.getMessage());
            }
        }
        List<Metric> metrics = parseValuesToMetrics(resourcesAvailability, currentTimestamp);
        return metrics;
    }

    protected void populateMetadata(Map<String, String> metadata, Pair<String, Float> p) {
        metadata.put(RESOURCE_LABEL, p.getKey().toLowerCase());
    }

    private Pair<String, Float> getResourceAvailabilityValue(ResourceType type) {
        LOGGER.debug("Getting audits from resource of type [" + type.getValue() + "]");
        Integer valueFailed = providerService
            .getAuditsFromResourceByState(OrderState.FAILED_ON_REQUEST, type, lastTimestampAwake);
        Integer valueOpen = providerService
            .getAuditsFromResourceByState(OrderState.OPEN, type, lastTimestampAwake);
        if(valueFailed == 0 && valueOpen == 0){
            throw new OrdersStateChangeNotFoundException("Not found resource data to calculate service success rate.");
        }
        Float availabilityData = AppUtil.percent(valueFailed, valueOpen);
        LOGGER.debug("Metric of availability data [" + availabilityData + "]");
        Pair<String, Float> pair = new Pair<>(type.getValue(), availabilityData);
        return pair;
    }
}
