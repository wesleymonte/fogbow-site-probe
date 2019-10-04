package cloud.fogbow.probes.core.probes.fogbow;

import cloud.fogbow.probes.core.PropertiesHolder;
import cloud.fogbow.probes.core.models.Metric;
import cloud.fogbow.probes.core.models.OrderState;
import cloud.fogbow.probes.core.models.ResourceType;
import cloud.fogbow.probes.core.probes.Probe;
import cloud.fogbow.probes.core.probes.exception.OrdersStateChangeNotFoundException;
import cloud.fogbow.probes.core.services.DataProviderService;
import cloud.fogbow.probes.core.utils.AppUtil;
import cloud.fogbow.probes.core.utils.Pair;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
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

public class FogbowResourceAvailabilityProbe implements Probe{

    private static final Logger LOGGER = LogManager
        .getLogger(FogbowResourceAvailabilityProbe.class);
    private static final String HELP = "Measures the level of failure to request a resource after the Order is open.";
    private static final String METRIC_NAME = "availability";
    private static final String RESOURCE_LABEL = "resource";
    private static final ResourceType[] resourceTypes = {ResourceType.COMPUTE, ResourceType.VOLUME,
        ResourceType.NETWORK};
    protected DataProviderService providerService;

    public FogbowResourceAvailabilityProbe() {
    }

    @Override
    public List<Metric> getMetrics(Timestamp timestamp) {
        List<Pair<String, Float>> resourcesAvailability = new ArrayList<>();
        for (ResourceType r : resourceTypes) {
            try {
                resourcesAvailability.add(getResourceAvailabilityValue(timestamp, r));
            } catch (Exception e){
                LOGGER.error(r.getValue() + ": " + e.getMessage());
            }
        }
        List<Metric> metrics = parseValuesToMetrics(resourcesAvailability, timestamp);
        return metrics;
    }

    @Override
    public void populateMetadata(Map<String, String> metadata, Pair<String, Float> p) {
        metadata.put(RESOURCE_LABEL, p.getKey().toLowerCase());
    }

    private Pair<String, Float> getResourceAvailabilityValue(Timestamp timestamp, ResourceType type) {
        LOGGER.debug("Getting audits from resource of type [" + type.getValue() + "]");
        Integer valueFailedAfterSuccessful = providerService
            .getAuditsFromResourceByState(OrderState.FAILED_AFTER_SUCCESSFUL_REQUEST, type,
                timestamp);
        Integer valueFulfilled = providerService
            .getAuditsFromResourceByState(OrderState.FULFILLED, type, timestamp);
        if(valueFailedAfterSuccessful == 0 && valueFulfilled == 0){
            throw new OrdersStateChangeNotFoundException("Not found resource data to calculate resource availability.");
        }
        Float availabilityData = AppUtil.percent(valueFailedAfterSuccessful,
            valueFulfilled);
        LOGGER.debug("Observation of availability data [" + availabilityData + "]");
        Pair<String, Float> pair = new Pair<>(type.getValue(), availabilityData);
        return pair;
    }

    private List<Metric> parseValuesToMetrics(List<Pair<String, Float>> values,
        Timestamp currentTimestamp) {
        List<Metric> metrics = new ArrayList<>();
        for (Pair<String, Float> p : values) {
            Metric m = parsePairToMetric(p, currentTimestamp);
            metrics.add(m);
        }
        return metrics;
    }

    private Metric parsePairToMetric(Pair<String, Float> p, Timestamp currentTimestamp) {
        Map<String, String> metadata = new HashMap<>();
        populateMetadata(metadata, p);
        metadata.put(FogbowProbe.targetLabelKey, PropertiesHolder.getInstance().getHostLabelProperty());
        metadata.put(FogbowProbe.probeTargetKey, PropertiesHolder.getInstance().getHostAddressProperty());
        Metric m = new Metric(p.getKey().toLowerCase() + "_" + METRIC_NAME, p.getValue(),
            currentTimestamp, HELP, metadata);
        return m;
    }

    public void setProviderService(DataProviderService providerService) {
        this.providerService = providerService;
    }
}
