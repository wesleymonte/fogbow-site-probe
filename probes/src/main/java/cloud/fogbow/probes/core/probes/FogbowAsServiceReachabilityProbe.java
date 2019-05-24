package cloud.fogbow.probes.core.probes;

import cloud.fogbow.probes.core.Constants;
import cloud.fogbow.probes.core.utils.PropertiesUtil;

import java.sql.Timestamp;

public class FogbowAsServiceReachabilityProbe extends FogbowServiceReachabilityProbe{

    public FogbowAsServiceReachabilityProbe() throws Exception{
        this.lastTimestampAwake = new Timestamp(System.currentTimeMillis());

        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath() + "private/";
        this.properties = new PropertiesUtil().readProperties(path + Constants.CONF_FILE);

        this.probeId = Integer.valueOf(properties.getProperty(Constants.AS_SERVICE_REACHABILITY_PROBE_ID));
        this.resourceId = Integer.valueOf(properties.getProperty(Constants.AS_SERVICE_REACHABILITY_RESOURCE_ID));
        this.SLEEP_TIME = Integer.valueOf(properties.getProperty(Constants.SLEEP_TIME));
        this.endPoint = properties.getProperty(Constants.AS_ENDPOINT);
    }

}
