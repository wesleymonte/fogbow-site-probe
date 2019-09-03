package cloud.fogbow.probes;

import cloud.fogbow.probes.core.probes.FogbowResourceAvailabilityProbe;
import cloud.fogbow.probes.core.probes.FogbowServiceLatencyProbe;
import cloud.fogbow.probes.core.probes.FogbowServiceReachabilityProbe;
import cloud.fogbow.probes.core.probes.FogbowServiceSuccessRateProbe;
import javax.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Main {

    private static final Logger LOGGER = LogManager.getLogger(Main.class);

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

        Thread firstProbe = new Thread(resourceAvailabilityProbe, "Thread-Resource-Availability-Probe");
        firstProbe.start();

        Thread secondProbe = new Thread(serviceLatencyProbe, "Thread-Service-Latency-Probe");
        secondProbe.start();

        Thread thirdProbe = new Thread(serviceAvailabilityProbe, "Thread-Service-Availability-Probe");
        thirdProbe.start();

        Thread fourthProbe = new Thread(serviceReachabilityProbe, "Thread-Service-Reachability-Probe");
        fourthProbe.start();
    }


}
