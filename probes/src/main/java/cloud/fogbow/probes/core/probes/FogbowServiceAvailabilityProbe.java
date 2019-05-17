package cloud.fogbow.probes.core.probes;

import cloud.fogbow.probes.core.Constants;
import cloud.fogbow.probes.core.models.Probe;
import cloud.fogbow.probes.core.utils.PropertiesUtil;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Component
public class FogbowServiceAvailabilityProbe extends Probe {

    public FogbowServiceAvailabilityProbe() throws Exception{
        this.lastTimestampAwake = new Timestamp(System.currentTimeMillis());

        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath() + "private/";
        this.properties = new PropertiesUtil().readProperties(path + Constants.CONF_FILE);

        this.probeId = Integer.valueOf(properties.getProperty(Constants.SERVICE_AVAILABILITY_PROBE_ID));
        this.resourceId = Integer.valueOf(properties.getProperty(Constants.SERVICE_AVAILABILITY_RESOURCE_ID));
    }

    public void run() {
        setup();

        while(true) {
            Number failedOrdersQuantity = providerService.getFailedOnRequest(lastTimestampAwake);
            Number openedOrdersQuantity = providerService.getOpened(lastTimestampAwake);

            this.lastTimestampAwake = new Timestamp(System.currentTimeMillis());

            List<Number> data = new ArrayList<>();
            data.add(failedOrdersQuantity);
            data.add(openedOrdersQuantity);
            System.out.println(failedOrdersQuantity);
            System.out.println(openedOrdersQuantity);

            sendMessage(data);

            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

    }
}
