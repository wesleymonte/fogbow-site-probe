package cloud.fogbow.probes.core.probes;

import cloud.fogbow.probes.core.fta.FtaConverter;
import cloud.fogbow.probes.core.models.Metric;
import cloud.fogbow.probes.core.models.OrderState;
import cloud.fogbow.probes.core.models.Probe;
import cloud.fogbow.probes.core.models.ResourceType;
import cloud.fogbow.probes.core.utils.Pair;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
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
public class FogbowServiceSuccessRateProbe extends Probe {

    private static final String PROBE_NAME = "service_success_rate";
    private static final Logger LOGGER = LogManager.getLogger(FogbowServiceSuccessRateProbe.class);
    private static final String HELP = "Measuring the success rate in requesting a resource.";
    public static final String THREAD_NAME = "Thread-Service-Success-Rate-Probe";
    private static final String PROBE_TYPE = "success_rate";

    public FogbowServiceSuccessRateProbe(Integer timeSleep, String ftaAddress) {
        super(timeSleep, ftaAddress);
    }

    public void run() {
        while (true) {
            LOGGER.info("----> Starting Fogbow Service Success Rate Probe");
            super.run();
        }
    }

    protected List<Metric> getMetrics(Timestamp currentTimestamp) {
        List<Pair<String, Float>> resourcesAvailability = new ArrayList<>();
        ResourceType resourceTypes[] = {ResourceType.COMPUTE, ResourceType.VOLUME,
            ResourceType.NETWORK};
        for (ResourceType r : resourceTypes) {
            resourcesAvailability.add(getResourceAvailabilityValue(r));
        }
        List<Metric> metrics = new ArrayList<>();
        parseValuesToMetrics(metrics, resourcesAvailability, currentTimestamp);
        LOGGER.info(
            "Made a metric with name at [" + currentTimestamp.toString()
                + "]");
        return metrics;
    }

    private void parseValuesToMetrics(List<Metric> metrics, List<Pair<String, Float>> values, Timestamp currentTimestamp){
        for(Pair<String, Float> p : values){
            Map<String, String> metadata = new HashMap<>();
            metadata.put("resource", p.getKey());
            Metric m = new Metric(PROBE_TYPE, p.getValue(), currentTimestamp, HELP, metadata);
            metrics.add(m);
        }
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
