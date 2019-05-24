package cloud.fogbow.probes.core.probes;

import cloud.fogbow.probes.core.models.Probe;

import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public abstract class FogbowServiceReachabilityProbe extends Probe {

    protected int SLEEP_TIME;
    protected String endPoint;
    private int successfulRequests = 0;
    private final int N_REQUESTS_PER_CICLE = 60;
    private final int RESPONSE_CODE_LOWER_BOUND = 200;
    private final int RESPONSE_CODE_UPPER_BOUND = 299;

    @Override
    public void run() {
        setup();

        while (true) {
            for(int i = 0; i < N_REQUESTS_PER_CICLE; i++) {
                doGetRequest();
                sleep(1000);
            }

            List<Number> data = new ArrayList<>();
            data.add(successfulRequests/N_REQUESTS_PER_CICLE);

            lastTimestampAwake = new Timestamp(System.currentTimeMillis());
            sendMessage(data);

            this.successfulRequests = 0;

            sleep(SLEEP_TIME);
        }
    }

    private void doGetRequest() {
        try {
            URL asUrl = new URL(this.endPoint);
            HttpURLConnection connection = (HttpURLConnection) asUrl.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();

            if (responseCode < RESPONSE_CODE_UPPER_BOUND && responseCode > RESPONSE_CODE_LOWER_BOUND) {
                successfulRequests++;
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private void sleep(int sleepTime) {
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

}
