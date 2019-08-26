package cloud.fogbow.probes.core.probes;

import cloud.fogbow.probes.core.Constants;
import cloud.fogbow.probes.core.models.FogbowDataProbe;
import cloud.fogbow.probes.core.models.OrderState;
import cloud.fogbow.probes.core.models.Probe;
import cloud.fogbow.probes.core.models.ResourceType;
import cloud.fogbow.probes.core.utils.PropertiesUtil;
import javafx.util.Pair;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Component
public class FogbowResourceAvailabilityProbe extends FogbowDataProbe {

    public FogbowResourceAvailabilityProbe() throws Exception{
        this.lastTimestampAwake = new Timestamp(System.currentTimeMillis());
        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath() + "private/";
        this.properties = new PropertiesUtil().readProperties(path + Constants.CONF_FILE);

        this.probeId = Integer.valueOf(properties.getProperty(Constants.RESOURCE_AVAILABILITY_PROBE_ID));
        this.firstTimeAwake = true;
        this.SLEEP_TIME = Integer.valueOf(properties.getProperty(Constants.SLEEP_TIME));
    }

    public void run() {
        setup();

        while(true) {
            Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

            List<List<Pair<Number, Timestamp>>> computeData = getResourceAvailabilityData(ResourceType.COMPUTE, currentTimestamp);
            List<List<Pair<Number, Timestamp>>> volumeData = getResourceAvailabilityData(ResourceType.VOLUME, currentTimestamp);
            List<List<Pair<Number, Timestamp>>> networkData = getResourceAvailabilityData(ResourceType.NETWORK, currentTimestamp);

            lastTimestampAwake = currentTimestamp;
            sendResourceDataMessages(computeData, volumeData, networkData);

        }

    }

    private List<List<Pair<Number, Timestamp>>> getResourceAvailabilityData(ResourceType type, Timestamp currentTimestamp) {
        List<List<Pair<Number, Timestamp>>> results;
        OrderState[] states = {OrderState.FAILED_AFTER_SUCCESSFUL_REQUEST, OrderState.FULFILLED};
        results = super.getData(states, type, currentTimestamp);
        return results;
    }
}
