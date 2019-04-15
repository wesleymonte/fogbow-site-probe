package cloud.fogbow.probes.core.models;

import cloud.fogbow.probes.core.Constants;
import cloud.fogbow.probes.core.services.DataProviderService;
import cloud.fogbow.probes.core.utils.PropertiesUtil;
import eu.atmosphere.tmaf.monitor.client.BackgroundClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class FogbowResourceAvailabilityProbe extends Probe {
    private static final String RESOURCE_AVAILABILITY_ID = "resource_availability_probe_id";

    private static final Logger LOGGER = LoggerFactory.getLogger(FogbowResourceAvailabilityProbe.class);

    public FogbowResourceAvailabilityProbe() {
        this.lastTimestampAwake = new Timestamp(System.currentTimeMillis());
    }

    public void run() {
        try {
            setup();
        } catch (Exception ex) {
            LOGGER.error("Error when setupping probe. Error message: " + ex.getMessage());
        }

        while(true) {
            System.out.println("running");
            int failedOrdersQuantity = providerService.getFailed(lastTimestampAwake);
            int fulfilledOrdersQuantity = providerService.getFulfilled(lastTimestampAwake);

            this.lastTimestampAwake = new Timestamp(System.currentTimeMillis());

            List<Integer> data = new ArrayList<>();
            data.add(failedOrdersQuantity);
            data.add(fulfilledOrdersQuantity);
            System.out.println(failedOrdersQuantity);
            System.out.println(fulfilledOrdersQuantity);

            sendMessage(data, new Timestamp(System.currentTimeMillis()));

            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

    }

    public void setup() throws Exception {
        this.client = new BackgroundClient("https://172.17.0.1:32025/monitor"); // that's it?
        client.authenticate(1, "pass".getBytes()); // what should be passed here?
        boolean startFlag = client.start();

        if(!startFlag) {
            System.out.println("failed on starting");
        }
    }

    public void createMessage() {
        this.message = this.client.createMessage();
        message.setResourceId(1); // what about here, is it important?
    }

    public DataProviderService getProviderService() {
        return this.providerService;
    }
}
