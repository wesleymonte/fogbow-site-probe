package cloud.fogbow.probes.core.probes;

import cloud.fogbow.probes.core.Constants;
import cloud.fogbow.probes.core.models.Probe;
import cloud.fogbow.probes.core.utils.PropertiesUtil;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Component
public class FogbowResourceAvailabilityProbe extends Probe {

    public FogbowResourceAvailabilityProbe() throws Exception{
        this.lastTimestampAwake = new Timestamp(System.currentTimeMillis());
        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath() + "private/";
        this.properties = new PropertiesUtil().readProperties(path + Constants.CONF_FILE);

        this.probeId = Integer.valueOf(properties.getProperty(Constants.RESOURCE_AVAILABILITY_PROBE_ID));
        this.resourceId = Integer.valueOf(properties.getProperty(Constants.RESOURCE_AVAILABILITY_RESOURCE_ID));
    }

    public void run() {
        setup();

        while(true) {
            Number failedOrdersQuantity = providerService.getFailed(lastTimestampAwake);
            Number fulfilledOrdersQuantity = providerService.getFulfilled(lastTimestampAwake);

            this.lastTimestampAwake = new Timestamp(System.currentTimeMillis());

            List<Number> data = new ArrayList<>();
            data.add(failedOrdersQuantity);
            data.add(fulfilledOrdersQuantity);
            System.out.println(failedOrdersQuantity);
            System.out.println(fulfilledOrdersQuantity);

            sendMessage(data);

            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

    }
}
