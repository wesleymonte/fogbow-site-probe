package cloud.fogbow.probes.core.probes;

import cloud.fogbow.probes.core.Constants;
import cloud.fogbow.probes.core.models.Probe;
import cloud.fogbow.probes.core.models.ResourceType;
import cloud.fogbow.probes.core.utils.PropertiesUtil;
import javafx.util.Pair;
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
            List<List<Pair<Number, Timestamp>>> computeData = getData(ResourceType.COMPUTE);
            List<List<Pair<Number, Timestamp>>> volumeData = getData(ResourceType.VOLUME);
            List<List<Pair<Number, Timestamp>>> networkData = getData(ResourceType.NETWORK);

            lastTimestampAwake = new Timestamp(System.currentTimeMillis());

            if(hasData(computeData))
                sendMessage(computeData);

            if(hasData(volumeData))
                sendMessage(volumeData);

            if(hasData(networkData))
                sendMessage(networkData);

            sleep(SLEEP_TIME);
        }

    }

    private List<List<Pair<Number, Timestamp>>> getData(ResourceType type) {
        List<List<Pair<Number, Timestamp>>> results = new ArrayList<>();

        List<Pair<Number, Timestamp>> l1 = new ArrayList<>();
        List<Pair<Number, Timestamp>> l2 = new ArrayList<>();
        List<Pair<Number, Timestamp>> l3 = new ArrayList<>();

        l1.add(new Pair(providerService.getFailed(lastTimestampAwake, firstTimeAwake, type).size(), lastTimestampAwake));
        l2.add(new Pair(providerService.getFulfilled(lastTimestampAwake, firstTimeAwake, type).size(), lastTimestampAwake));
        l3.add(new Pair(new Timestamp(System.currentTimeMillis()).getTime() - lastTimestampAwake.getTime(), new Timestamp(System.currentTimeMillis())));

        results.add(l1);
        results.add(l2);
        results.add(l3);
        this.firstTimeAwake = false;
        return results;
    }
}
