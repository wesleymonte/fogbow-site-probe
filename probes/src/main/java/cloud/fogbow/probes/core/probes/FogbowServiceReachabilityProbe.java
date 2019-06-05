package cloud.fogbow.probes.core.probes;

import cloud.fogbow.probes.core.Constants;
import cloud.fogbow.probes.core.models.Probe;
import cloud.fogbow.probes.core.utils.PropertiesUtil;
import javafx.util.Pair;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class FogbowServiceReachabilityProbe extends Probe {

    protected int SLEEP_TIME;
    protected String endPoint;
    private int successfulRequests = 0;
    private final int N_REQUESTS_PER_CICLE = 60;
    private final int RESPONSE_CODE_LOWER_BOUND = 200;
    private final int RESPONSE_CODE_UPPER_BOUND = 299;
    private String AS_ENDPOINT;
    private String RAS_ENDPOINT;
    private String FNS_ENDPOINT;
    private String MS_ENDPOINT;

    public FogbowServiceReachabilityProbe() throws Exception{
        this.lastTimestampAwake = new Timestamp(System.currentTimeMillis());

        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath() + "private/";
        this.properties = new PropertiesUtil().readProperties(path + Constants.CONF_FILE);

        this.probeId = Integer.valueOf(properties.getProperty(Constants.SERVICE_REACHABILITY_PROBE_ID));
        this.resourceId = Integer.valueOf(properties.getProperty(Constants.SERVICE_REACHABILITY_RESOURCE_ID));
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
                sleep(1000);
            }

            List<Pair<Number, Timestamp>> data = new ArrayList<>();
            data.add(new Pair<>(successfulRequests/N_REQUESTS_PER_CICLE, lastTimestampAwake));

            List<List<Pair<Number, Timestamp>>> dataWrapper = new ArrayList<>();
            dataWrapper.add(data);
            sendMessage(dataWrapper);

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

            if (successfulRequests(asResponseCode, rasResponseCode, fnsResponseCode, msResponseCode)) {
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

    private boolean successfulRequests(int asResponseCode, int rasResponseCode, int fnsResponseCode, int msResponseCode) {
        if(asResponseCode < RESPONSE_CODE_UPPER_BOUND && asResponseCode > RESPONSE_CODE_LOWER_BOUND
            && rasResponseCode < RESPONSE_CODE_UPPER_BOUND && rasResponseCode > RESPONSE_CODE_LOWER_BOUND
            && fnsResponseCode < RESPONSE_CODE_UPPER_BOUND && fnsResponseCode > RESPONSE_CODE_LOWER_BOUND
            && msResponseCode < RESPONSE_CODE_UPPER_BOUND && msResponseCode > RESPONSE_CODE_LOWER_BOUND) {
            return true;
        }

        return false;
    }

}
