package cloud.fogbow.probes;

import cloud.fogbow.probes.core.probes.FogbowResourceAvailabilityProbe;
import cloud.fogbow.probes.core.probes.FogbowServiceLatencyProbe;
import cloud.fogbow.probes.core.probes.FogbowServiceReachabilityProbe;
import cloud.fogbow.probes.core.probes.FogbowServiceSuccessRateProbe;
import cloud.fogbow.probes.core.utils.ProbeConstants.Properties;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    @Autowired
    FogbowResourceAvailabilityProbe resourceAvailabilityProbe;

    @Autowired
    FogbowServiceLatencyProbe serviceLatencyProbe;

    @Autowired
    FogbowServiceSuccessRateProbe serviceAvailabilityProbe;

    @Autowired
    FogbowServiceReachabilityProbe serviceReachabilityProbe;

    @PostConstruct
    public void startProbes() {

        Thread firstProbe = new Thread(resourceAvailabilityProbe);
        firstProbe.start();

        Thread secondProbe = new Thread(serviceLatencyProbe);
        secondProbe.start();

        Thread thirdProbe = new Thread(serviceAvailabilityProbe);
        thirdProbe.start();

        Thread asReachabilityProbe = new Thread(serviceReachabilityProbe);
        asReachabilityProbe.start();
    }


}
