package cloud.fogbow.probes.core.probes;

import cloud.fogbow.probes.core.Constants;
import cloud.fogbow.probes.core.models.Probe;
import cloud.fogbow.probes.core.utils.PropertiesUtil;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;

@Component
public class FogbowServiceLatencyProbe extends Probe {

    public FogbowServiceLatencyProbe() throws Exception{
        this.lastTimestampAwake = new Timestamp(System.currentTimeMillis());

        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath() + "private/";
        this.properties = new PropertiesUtil().readProperties(path + Constants.CONF_FILE);

        this.probeId = Integer.valueOf(properties.getProperty(Constants.SERVICE_LATENCY_PROBE_ID));
        this.resourceId = Integer.valueOf(properties.getProperty(Constants.SERVICE_LATENCY_RESOURCE_ID));
        this.firstTimeAwake = true;
    }

    public void run() {
        setup();

        while(true) {
            List<Number> latencies = this.providerService.getLatencies(lastTimestampAwake, firstTimeAwake);
            this.firstTimeAwake = false;
            this.lastTimestampAwake = new Timestamp(System.currentTimeMillis());
            for (Number n : latencies) {
                System.out.println("LATENCIES");
                System.out.println(n);
            }
            if(!latencies.isEmpty())
                sendMessage(latencies);

            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}
