package cloud.fogbow.probes.core.probes;

import cloud.fogbow.probes.core.Constants;
import cloud.fogbow.probes.core.models.Probe;
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
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class FogbowServiceReachabilityProbe extends Probe {

    private static final Logger LOGGER = Logger.getLogger(FogbowServiceReachabilityProbe.class);
    private final int N_REQUESTS_PER_CICLE = 1;
    private final int RESPONSE_CODE_LOWER_BOUND = 199;
    private final int RESPONSE_CODE_UPPER_BOUND = 300;
    protected int SLEEP_TIME;
    private int successfulRequests = 0;
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
        setup();

        while (true) {
            lastTimestampAwake = new Timestamp(System.currentTimeMillis());

            for (int i = 0; i < N_REQUESTS_PER_CICLE; i++) {
                doGetRequest();
            }

            List<Pair<Number, Timestamp>> data = new ArrayList<>();
            data.add(new Pair<>(successfulRequests / N_REQUESTS_PER_CICLE, lastTimestampAwake));

            List<List<Pair<Number, Timestamp>>> dataWrapper = new ArrayList<>();
            dataWrapper.add(data);
            int resourceId = Integer.valueOf(properties.getProperty(Constants.SITE_RESOURCE_ID));
            sendMessage(resourceId, dataWrapper);

            this.successfulRequests = 0;

            sleep(SLEEP_TIME);
        }
    }

    private void doGetRequest() {
        try {
            Map<String, Integer> httpCodes = this.getHttpCodes();
            boolean someFailed = this.checkHttpCodes(httpCodes);
            if (!someFailed) {
                successfulRequests++;
            }
        } catch (Exception e) {
            LOGGER.error("Error while checking reachability of services", e);
        }
    }

    public Map<String, Integer> getHttpCodes() throws IOException {
        Map<String, Integer> httpCodes = new HashMap<>();
        for (Service service : services.values()) {
            Integer response = getResponseCode(service.ENDPOINT);
            httpCodes.put(service.ID, response);
        }
        return httpCodes;
    }

    private int getResponseCode(String endPoint) throws IOException {
        URL url = new URL(endPoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        return connection.getResponseCode();
    }

    public boolean checkHttpCodes(Map<String, Integer> httpCodes) {
        boolean someFailed = false;
        for (Entry<String, Integer> code : httpCodes.entrySet()) {
            Service service = services.get(code.getKey());
            if (hasFailed(code.getValue())) {
                String date = timestampToDate(Instant.now().getEpochSecond());
                LOGGER.error("[" + date + "] : " + service.LABEL + " is down");
                someFailed = true;
            }
        }
        return someFailed;
    }

    private boolean hasFailed(int responseCode) {
        return responseCode > RESPONSE_CODE_UPPER_BOUND || responseCode < RESPONSE_CODE_LOWER_BOUND;
    }

    private String timestampToDate(long timestamp) {
        Date date = new java.util.Date(timestamp * 1000L);
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT-3"));
        return sdf.format(date);
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
