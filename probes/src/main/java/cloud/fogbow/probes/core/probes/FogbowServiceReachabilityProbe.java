package cloud.fogbow.probes.core.probes;

import cloud.fogbow.probes.core.Constants;
import cloud.fogbow.probes.core.models.Probe;
import javafx.util.Pair;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class FogbowServiceReachabilityProbe extends Probe {

    protected int SLEEP_TIME;
    private int successfulRequests = 0;
    private final int N_REQUESTS_PER_CICLE = 1;
    private final int RESPONSE_CODE_LOWER_BOUND = 199;
    private final int RESPONSE_CODE_UPPER_BOUND = 300;
    private String AS_ENDPOINT;
    private String RAS_ENDPOINT;
    private String FNS_ENDPOINT;
    private String MS_ENDPOINT;

    @PostConstruct
    public void FogbowServiceReachabilityProbe() {
        this.lastTimestampAwake = new Timestamp(System.currentTimeMillis());
        this.probeId = Integer.valueOf(properties.getProperty(Constants.SERVICE_REACHABILITY_PROBE_ID));
        this.SLEEP_TIME = Integer.valueOf(properties.getProperty(Constants.SLEEP_TIME));
        this.AS_ENDPOINT = properties.getProperty(Constants.AS_ENDPOINT);
        this.RAS_ENDPOINT = properties.getProperty(Constants.RAS_ENDPOINT);
        this.FNS_ENDPOINT = properties.getProperty(Constants.FNS_ENDPOINT);
        this.MS_ENDPOINT = properties.getProperty(Constants.MS_ENDPOINT);
    }

    @Override
    public void run() {
        setup();

        while (true) {
            lastTimestampAwake = new Timestamp(System.currentTimeMillis());

            for(int i = 0; i < N_REQUESTS_PER_CICLE; i++) {
                doGetRequest();
            }

            List<Pair<Number, Timestamp>> data = new ArrayList<>();
            data.add(new Pair<>(successfulRequests/N_REQUESTS_PER_CICLE, lastTimestampAwake));

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
            int asResponseCode = getResponseCode(AS_ENDPOINT);
            int rasResponseCode = getResponseCode(RAS_ENDPOINT);
            int fnsResponseCode = getResponseCode(FNS_ENDPOINT);
            int msResponseCode = getResponseCode(MS_ENDPOINT);

            loggerHandler(asResponseCode, rasResponseCode, fnsResponseCode, msResponseCode);

            if (checkRequests(asResponseCode, rasResponseCode, fnsResponseCode, msResponseCode)) {
                successfulRequests++;
            }

        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private int getResponseCode(String endPoint) throws IOException {
        URL url = new URL(endPoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        return connection.getResponseCode();
    }

    private boolean checkRequests(int asResponseCode, int rasResponseCode, int fnsResponseCode, int msResponseCode) {
        if(asResponseCode < RESPONSE_CODE_UPPER_BOUND && asResponseCode > RESPONSE_CODE_LOWER_BOUND
            && rasResponseCode < RESPONSE_CODE_UPPER_BOUND && rasResponseCode > RESPONSE_CODE_LOWER_BOUND
            && fnsResponseCode < RESPONSE_CODE_UPPER_BOUND && fnsResponseCode > RESPONSE_CODE_LOWER_BOUND
            && msResponseCode < RESPONSE_CODE_UPPER_BOUND && msResponseCode > RESPONSE_CODE_LOWER_BOUND) {
            return true;
        }

        return false;
    }

    private boolean hasFailed(int responseCode) {
        return responseCode > RESPONSE_CODE_UPPER_BOUND || responseCode < RESPONSE_CODE_LOWER_BOUND;
    }

    private void loggerHandler(int asResponseCode, int rasResponseCode, int fnsResponseCode, int msResponseCode) {
        List<String> messages = new ArrayList<>();

        if(hasFailed(asResponseCode)) {
            messages.add("Authentication Service is down");
        }

        if(hasFailed(rasResponseCode)) {
            messages.add("Resource Allocation Service is down");
        }

        if(hasFailed(fnsResponseCode)) {
            messages.add("Federated Network Service is down");
        }

        if(hasFailed(msResponseCode)) {
            messages.add("Membership Service is down");
        }

        for(int i = 0; i < messages.size(); i++) {
            String date = timestampToDate(Instant.now().getEpochSecond());
            String currentMessage = "[" + date + "] " + messages.get(i) + "\n";
            try {
                if (!Files.exists(Paths.get("probes-log.log"), LinkOption.NOFOLLOW_LINKS))
                    Files.createFile(Paths.get("probes-log.log"));
                Files.write(Paths.get("probes-log.log"), currentMessage.getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                //exception handling left as an exercise for the reader
            }
        }

    }

    private String timestampToDate(long timestamp){
        Date date = new java.util.Date(timestamp*1000L);
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT-3"));
        return sdf.format(date);
    }

}
