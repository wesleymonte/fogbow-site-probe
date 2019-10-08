package cloud.fogbow.probes.core.probes.fogbow.collectors;

import cloud.fogbow.probes.core.models.Metric;
import cloud.fogbow.probes.core.models.OrderState;
import cloud.fogbow.probes.core.models.ResourceType;
import cloud.fogbow.probes.core.probes.MetricCollector;
import cloud.fogbow.probes.core.probes.fogbow.exceptions.OrdersStateChangeNotFoundException;
import cloud.fogbow.probes.core.probes.fogbow.util.FogbowProbeUtils;
import cloud.fogbow.probes.provider.DataProviderService;
import cloud.fogbow.probes.core.utils.AppUtil;
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

public class FogbowResourceAvailabilityMetricCollector implements MetricCollector {

    private static final Logger LOGGER = LogManager
        .getLogger(FogbowResourceAvailabilityMetricCollector.class);
    private static final String HELP = "Measures the level of failure to request a resource after the Order is open.";
    private static final String METRIC_NAME = "availability";
    private static final String RESOURCE_LABEL = "resource";
    private static final String SERVICE_LABEL = "service";
    private static final String SERVICE_NAME = "ras";
    private static final ResourceType[] resourceTypes = {ResourceType.COMPUTE, ResourceType.VOLUME,
        ResourceType.NETWORK};

    protected DataProviderService providerService;

    public FogbowResourceAvailabilityMetricCollector(DataProviderService providerService) {
        this.providerService = providerService;
    }

    @Override
    public List<Metric> collect(Timestamp timestamp) {
        List<Pair<String, Float>> resourcesAvailability = new ArrayList<>();
        for (ResourceType r : resourceTypes) {
            try {
                resourcesAvailability.add(getResourceAvailabilityValue(timestamp, r));
            } catch (Exception e) {
                LOGGER.error(r.getValue() + ": " + e.getMessage());
            }
        }
        List<Metric> metrics = FogbowProbeUtils
            .parsePairsToMetrics(this, resourcesAvailability, timestamp);
        return metrics;
    }

    @Override
    public String getMetricName() {
        return METRIC_NAME;
    }

    @Override
    public String getHelp() {
        return HELP;
    }

    @Override
    public void populateMetadata(Map<String, String> metadata, Pair<String, Float> p) {
        metadata.put(RESOURCE_LABEL, p.getKey().toLowerCase());
        metadata.put(SERVICE_LABEL, SERVICE_NAME);
    }

    private Pair<String, Float> getResourceAvailabilityValue(Timestamp timestamp,
        ResourceType type) {
        LOGGER.debug("Getting audits from resource of type [" + type.getValue() + "]");
        Integer valueFailedAfterSuccessful = providerService
            .getAuditsFromResourceByState(OrderState.FAILED_AFTER_SUCCESSFUL_REQUEST, type,
                timestamp);
        Integer valueFulfilled = providerService
            .getAuditsFromResourceByState(OrderState.FULFILLED, type, timestamp);
        if (valueFailedAfterSuccessful == 0 && valueFulfilled == 0) {
            throw new OrdersStateChangeNotFoundException(
                "Not found resource data to calculate resource availability.");
        }
        Float availabilityData = AppUtil.percent(valueFailedAfterSuccessful, valueFulfilled);
        LOGGER.debug("Observation of availability data [" + availabilityData + "]");
        Pair<String, Float> pair = new Pair<>(type.getValue(), availabilityData);
        return pair;
    }
}
