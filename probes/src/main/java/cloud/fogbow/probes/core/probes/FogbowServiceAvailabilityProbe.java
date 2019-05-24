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

    private int SLEEP_TIME;

    public FogbowServiceAvailabilityProbe() throws Exception{
        this.lastTimestampAwake = new Timestamp(System.currentTimeMillis());

        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath() + "private/";
        this.properties = new PropertiesUtil().readProperties(path + Constants.CONF_FILE);

        this.probeId = Integer.valueOf(properties.getProperty(Constants.SERVICE_AVAILABILITY_PROBE_ID));
        this.resourceId = Integer.valueOf(properties.getProperty(Constants.SERVICE_AVAILABILITY_RESOURCE_ID));
        this.firstTimeAwake = true;
        this.SLEEP_TIME = Integer.valueOf(properties.getProperty(Constants.SLEEP_TIME));
    }

    public void run() {
        setup();

        while(true) {
            List<Number> data = getData();

            this.lastTimestampAwake = new Timestamp(System.currentTimeMillis());
            System.out.println("SERVICE AVAILABILITY");
            System.out.println(data.get(0));
            System.out.println(data.get(1));
            sendMessage(data);

            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

    }

    private List<Number> getData() {
        List<Number> results = new ArrayList<>();

        results.add(providerService.getFailedOnRequest(lastTimestampAwake, firstTimeAwake).size());
        results.add(providerService.getOpened(lastTimestampAwake, firstTimeAwake).size());
        this.firstTimeAwake = false;
        return results;
    }
}
