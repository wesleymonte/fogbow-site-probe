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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FogbowServiceSuccessRateProbe is responsible for measuring the success rate in requesting a
 * resource. The success rate is measured by the relationship between the number of orders that
 * failed after requests ({@link OrderState#FAILED_AFTER_SUCCESSFUL_REQUEST}) and the number of
 * orders opened ({@link OrderState#OPEN}).
 */
public class FogbowServiceSuccessRateMetricCollector implements MetricCollector {

    private static final Logger LOGGER = LogManager.getLogger(
        FogbowServiceSuccessRateMetricCollector.class);
    private static final String HELP = "The success rate in requesting a resource.";
    private static final String METRIC_NAME = "success_rate";
    private static final String RESOURCE_LABEL = "resource";
    protected DataProviderService providerService;
    private static final ResourceType[] resourceTypes = {ResourceType.COMPUTE, ResourceType.VOLUME,
        ResourceType.NETWORK};

    public FogbowServiceSuccessRateMetricCollector(DataProviderService providerService) {
        this.providerService = providerService;
    }

    public List<Metric> collect(Timestamp timestamp) {
        List<Metric> metrics = new ArrayList<>();
        List<Pair<String, Float>> resourcesAvailability = new ArrayList<>();
        Set<String> cloudNames = providerService.getCloudNamesAfterTimestamp(timestamp);
        Iterator<String> i = cloudNames.iterator();
        while(i.hasNext()){
            String cloud = i.next();
            for (ResourceType r : resourceTypes) {
                try {
                    resourcesAvailability.add(getResourceAvailabilityValue(cloud, timestamp, r));
                } catch (Exception e) {
                    LOGGER.error(r.getValue() + ": " + e.getMessage());
                }
            }
            List<Metric> m = FogbowProbeUtils
                .parsePairsToMetrics(this, resourcesAvailability, cloud, timestamp);
            metrics.addAll(m);
        }
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
    }

    private Pair<String, Float> getResourceAvailabilityValue(String cloudName, Timestamp timestamp,
        ResourceType type) {
        LOGGER.debug("Getting audits from resource of type [" + type.getValue() + "]");
        Integer valueFailed = providerService
            .getAuditsFromResourceByState(cloudName, OrderState.FAILED_ON_REQUEST, type, timestamp);
        Integer valueOpen = providerService
            .getAuditsFromResourceByState(cloudName, OrderState.OPEN, type, timestamp);
        if (valueFailed == 0 && valueOpen == 0) {
            throw new OrdersStateChangeNotFoundException(
                "Not found resource data to calculate service success rate.");
        }
        Float availabilityData = AppUtil.percent(valueFailed, valueOpen);
        LOGGER.debug("Metric of availability data [" + availabilityData + "]");
        Pair<String, Float> pair = new Pair<>(type.getValue(), availabilityData);
        return pair;
    }
}
