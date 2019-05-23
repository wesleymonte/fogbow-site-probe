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

    private int SLEEP_TIME;

    public FogbowResourceAvailabilityProbe() throws Exception{
        this.lastTimestampAwake = new Timestamp(System.currentTimeMillis());
        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath() + "private/";
        this.properties = new PropertiesUtil().readProperties(path + Constants.CONF_FILE);

        this.probeId = Integer.valueOf(properties.getProperty(Constants.RESOURCE_AVAILABILITY_PROBE_ID));
        this.resourceId = Integer.valueOf(properties.getProperty(Constants.RESOURCE_AVAILABILITY_RESOURCE_ID));
        this.firstTimeAwake = true;
        this.SLEEP_TIME = Integer.valueOf(properties.getProperty(Constants.SLEEP_TIME));
    }

    public void run() {
        setup();

        while(true) {
            List<Number> data = getData();

            this.lastTimestampAwake = new Timestamp(System.currentTimeMillis());
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

        results.add(providerService.getFailed(lastTimestampAwake, firstTimeAwake).size());
        results.add(providerService.getFulfilled(lastTimestampAwake, firstTimeAwake).size());
        this.firstTimeAwake = false;
        return results;
    }
}
