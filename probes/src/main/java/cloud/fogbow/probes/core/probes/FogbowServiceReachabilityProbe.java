package cloud.fogbow.probes.core.probes;

import cloud.fogbow.probes.core.fta.FtaConverter;
import cloud.fogbow.probes.core.models.Metric;
import cloud.fogbow.probes.core.models.Probe;
import cloud.fogbow.probes.core.utils.AppUtil;
import cloud.fogbow.probes.core.utils.Pair;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FogbowServiceReachabilityProbe is responsible for monitoring the availability of Fogbow services.
 * Availability is verified by performing http requests to services at specific verification
 * addresses.
 */
public class FogbowServiceReachabilityProbe extends Probe {

    public static final String THREAD_NAME = "Thread-Service-Reachability-Probe";
    private static final Logger LOGGER = LogManager.getLogger(FogbowServiceReachabilityProbe.class);
    private static final String PROBE_NAME = "service_reachability";
    private static final String HELP = "Monitoring the availability of Fogbow services.";
    private final int RESPONSE_CODE_LOWER_BOUND = 199;
    private final int RESPONSE_CODE_UPPER_BOUND = 300;
    private String AS_ENDPOINT;
    private String RAS_ENDPOINT;
    private String FNS_ENDPOINT;
    private String MS_ENDPOINT;
    private Map<String, FogbowService> services;

    public FogbowServiceReachabilityProbe(Integer timeSleep, String ftaAddress, String asEndpoint,
        String rasEndpoint, String fnsEndpoint, String msEndpoint) {
        super(timeSleep, ftaAddress);
        this.AS_ENDPOINT = asEndpoint;
        this.RAS_ENDPOINT = rasEndpoint;
        this.FNS_ENDPOINT = fnsEndpoint;
        this.MS_ENDPOINT = msEndpoint;
        this.services = Collections.unmodifiableMap(buildServices());
    }

    private Map<String, FogbowService> buildServices() {
        Map<String, FogbowService> services = new HashMap<>();
        final String AS_ID = "AS";
        final String RAS_ID = "RAS";
        final String FNS_ID = "FNS";
        final String MS_ID = "MS";

        FogbowService AS_SERVICE = new FogbowService(AS_ID, "Authentication Service", AS_ENDPOINT);
        FogbowService RAS_SERVICE = new FogbowService(RAS_ID, "Resource Allocation Service",
            RAS_ENDPOINT);
        FogbowService FNS_SERVICE = new FogbowService(FNS_ID, "Federated Network Service",
            FNS_ENDPOINT);
        FogbowService MS_SERVICE = new FogbowService(MS_ID, "Membership Service", MS_ENDPOINT);

        services.put(AS_ID, AS_SERVICE);
        services.put(RAS_ID, RAS_SERVICE);
        services.put(FNS_ID, FNS_SERVICE);
        services.put(MS_ID, MS_SERVICE);

        return services;
    }

    @Override
    public void run() {
        while (true) {
            LOGGER.info("----> Starting Fogbow Service Reachability Probe...");
            super.run();
        }
    }

    protected Metric getMetric(Timestamp currentTimestamp) {
        Map<String, Boolean> result = doGetRequest();
        List<Pair<String, Float>> values = toValues(result);
        Metric metric = FtaConverter.createMetric(PROBE_NAME, values, currentTimestamp, HELP);
        LOGGER.info(
            "Made a metric with name [" + metric.getName() + "] at [" + currentTimestamp.toString()
                + "]");
        return metric;
    }

    private List<Pair<String, Float>> toValues(Map<String, Boolean> result) {
        List<Pair<String, Float>> values = new ArrayList<>();
        for (Entry<String, Boolean> e : result.entrySet()) {
            Pair<String, Float> p = new Pair<>(e.getKey(), parseToFloat(e.getValue()));
            values.add(p);
        }
        return values;
    }

    private Float parseToFloat(boolean b) {
        if (b) {
            return (float) 1;
        } else {
            return (float) 0;
        }
    }

    private Map<String, Boolean> doGetRequest() {
        Map<String, Integer> httpCodes = this.getHttpCodes();
        Map<String, Boolean> result = this.checkHttpCodes(httpCodes);
        return result;
    }

    private Map<String, Integer> getHttpCodes() {
        Map<String, Integer> httpCodes = new HashMap<>();
        for (FogbowService service : services.values()) {
            try {
                Integer response = getResponseCode(service.ENDPOINT);
                httpCodes.put(service.ID, response);
                LOGGER.debug("Http code [" + response + "] of service [" + service.LABEL + "]");
            } catch (IOException e) {
                LOGGER.error(
                    "Error while do get request to fogbow service [" + service.LABEL + "]: " + e
                        .getMessage());
            }
        }
        return httpCodes;
    }

    private int getResponseCode(String endPoint) throws IOException {
        URL url = new URL(endPoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        return connection.getResponseCode();
    }

    private Map<String, Boolean> checkHttpCodes(Map<String, Integer> httpCodes) {
        Map<String, Boolean> result = new HashMap<>();
        for (Entry<String, Integer> code : httpCodes.entrySet()) {
            FogbowService service = services.get(code.getKey());
            String date = AppUtil.timestampToDate(Instant.now().getEpochSecond());
            if (hasFailed(code.getValue())) {
                LOGGER.error("[" + date + "] : " + service.LABEL + " is down");
                result.put(service.ID, false);
            } else {
                LOGGER.error("[" + date + "] : " + service.LABEL + " is up");
                result.put(service.ID, true);
            }
        }
        return result;
    }

    private boolean hasFailed(int responseCode) {
        return responseCode > RESPONSE_CODE_UPPER_BOUND || responseCode < RESPONSE_CODE_LOWER_BOUND;
    }

    private class FogbowService {

        private final String ID;
        private final String LABEL;
        private final String ENDPOINT;

        public FogbowService(String ID, String LABEL, String ENDPOINT) {
            this.ID = ID;
            this.LABEL = LABEL;
            this.ENDPOINT = ENDPOINT;
        }
    }


}
