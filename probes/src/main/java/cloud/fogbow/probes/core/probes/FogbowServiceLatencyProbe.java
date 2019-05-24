package cloud.fogbow.probes.core.probes;

import cloud.fogbow.probes.core.Constants;
import cloud.fogbow.probes.core.models.Probe;
import cloud.fogbow.probes.core.utils.PropertiesUtil;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Component
public class FogbowServiceLatencyProbe extends Probe {

    private int SLEEP_TIME;

    public FogbowServiceLatencyProbe() throws Exception{
        this.lastTimestampAwake = new Timestamp(System.currentTimeMillis());

        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath() + "private/";
        this.properties = new PropertiesUtil().readProperties(path + Constants.CONF_FILE);

        this.probeId = Integer.valueOf(properties.getProperty(Constants.SERVICE_LATENCY_PROBE_ID));
        this.resourceId = Integer.valueOf(properties.getProperty(Constants.SERVICE_LATENCY_RESOURCE_ID));
        this.firstTimeAwake = true;
        this.SLEEP_TIME = Integer.valueOf(properties.getProperty(Constants.SLEEP_TIME));
    }

    public void run() {
        setup();

        while(true) {
            List<Number> latencies = this.providerService.getLatencies(lastTimestampAwake, firstTimeAwake);

            List<List<Number>> latenciesWrapper = new ArrayList<>();
            latenciesWrapper.add(latencies);

            this.firstTimeAwake = false;
            this.lastTimestampAwake = new Timestamp(System.currentTimeMillis());

            if(!latencies.isEmpty())
                sendMessage(latenciesWrapper);

            sleep(SLEEP_TIME);
        }
    }
}
