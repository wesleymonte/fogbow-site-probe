package cloud.fogbow.probes.core.probes;

import cloud.fogbow.probes.core.Constants;
import cloud.fogbow.probes.core.fta.FtaSender;
import cloud.fogbow.probes.core.models.Observation;
import cloud.fogbow.probes.core.models.Probe;
import cloud.fogbow.probes.core.utils.AppUtil;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javafx.util.Pair;
import javax.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class FogbowServiceReachabilityProbe extends Probe {

    private static final Logger LOGGER = LogManager.getLogger(FogbowServiceReachabilityProbe.class);
    private static final String PROBE_LABEL = "service_reachability_probe";
    private final int RESPONSE_CODE_LOWER_BOUND = 199;
    private final int RESPONSE_CODE_UPPER_BOUND = 300;
    private String AS_ENDPOINT;
    private String RAS_ENDPOINT;
    private String FNS_ENDPOINT;
    private String MS_ENDPOINT;
    private Map<String, Service> services;

    @PostConstruct
    public void FogbowServiceReachabilityProbe() {
        this.lastTimestampAwake = new Timestamp(System.currentTimeMillis());
        this.probeId = Integer
            .valueOf(properties.getProperty(Constants.SERVICE_REACHABILITY_PROBE_ID));
        this.SLEEP_TIME = Integer.valueOf(properties.getProperty(Constants.SLEEP_TIME));
        this.AS_ENDPOINT = properties.getProperty(Constants.AS_ENDPOINT);
        this.RAS_ENDPOINT = properties.getProperty(Constants.RAS_ENDPOINT);
        this.FNS_ENDPOINT = properties.getProperty(Constants.FNS_ENDPOINT);
        this.MS_ENDPOINT = properties.getProperty(Constants.MS_ENDPOINT);
        this.services = Collections.unmodifiableMap(buildServices());
        this.FTA_ADDRESS = properties.getProperty(Constants.FMA_ADDRESS).trim();
    }

    private Map<String, Service> buildServices() {
        Map<String, Service> services = new HashMap<>();
        final String AS_ID = "AS";
        final String RAS_ID = "RAS";
        final String FNS_ID = "FNS";
        final String MS_ID = "MS";

        Service AS_SERVICE = new Service(AS_ID, "Authentication Service", AS_ENDPOINT);
        Service RAS_SERVICE = new Service(RAS_ID, "Resource Allocation Service", RAS_ENDPOINT);
        Service FNS_SERVICE = new Service(FNS_ID, "Federated Network Service", FNS_ENDPOINT);
        Service MS_SERVICE = new Service(MS_ID, "Membership Service", MS_ENDPOINT);

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
            Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
            Observation observation = makeObservation(currentTimestamp);
            LOGGER.info("Probe[" + this.probeId + "] made a observation at [" + observation.getTimestamp().toString() + "]");
            FtaSender.sendObservation(FTA_ADDRESS, observation);
            lastTimestampAwake = currentTimestamp;
            sleep(SLEEP_TIME);
        }
    }

    private Observation makeObservation(Timestamp currentTimestamp) {
        Map<String, Boolean> result = doGetRequest();
        List<Pair<String, Float>> values = toValues(result);
        Observation observation = new Observation(PROBE_LABEL, values, currentTimestamp);
        LOGGER.info("Made a observation with label [" + observation.getLabel() + "] at [" + currentTimestamp.toString() + "]");
        return observation;
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
        if (b) return (float) 1;
        else return (float) 0;
    }

    private Map<String, Boolean> doGetRequest() {
        Map<String, Boolean> result = new HashMap<>();
        try {
            Map<String, Integer> httpCodes = this.getHttpCodes();
            result = this.checkHttpCodes(httpCodes);
        } catch (Exception e) {
            LOGGER.error("Error while checking reachability of services", e);
        }
        return result;
    }

    private Map<String, Integer> getHttpCodes() throws IOException {
        Map<String, Integer> httpCodes = new HashMap<>();
        for (Service service : services.values()) {
            Integer response = getResponseCode(service.ENDPOINT);
            httpCodes.put(service.ID, response);
            LOGGER.debug("Http code [" + response + "] of service [" + service.LABEL + "]");
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
            Service service = services.get(code.getKey());
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

    private class Service {

        private final String ID;
        private final String LABEL;
        private final String ENDPOINT;

        public Service(String ID, String LABEL, String ENDPOINT) {
            this.ID = ID;
            this.LABEL = LABEL;
            this.ENDPOINT = ENDPOINT;
        }
    }


}
