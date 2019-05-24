package cloud.fogbow.probes;

import cloud.fogbow.probes.core.probes.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class Main {

        @Autowired
        FogbowResourceAvailabilityProbe resourceAvailabilityProbe;

        @Autowired
        FogbowServiceLatencyProbe serviceLatencyProbe;

        @Autowired
        FogbowServiceAvailabilityProbe serviceAvailabilityProbe;

        @Autowired
        FogbowAsServiceReachabilityProbe asServiceReachabilityProbe;

        @Autowired
        FogbowRasServiceReachabilityProbe rasServiceReachabilityProbe;

        @Autowired
        FogbowMsServiceReachabilityProbe msServiceReachabilityProbe;

        @Autowired
        FogbowFnsServiceReachabilityProbe fnsServiceReachabilityProbe;

        @PostConstruct
        public void startProbes() {
            Thread firstProbe = new Thread(resourceAvailabilityProbe);
            firstProbe.start();

            Thread secondProbe = new Thread(serviceLatencyProbe);
            secondProbe.start();

            Thread thirdProbe = new Thread(serviceAvailabilityProbe);
            thirdProbe.start();

            Thread asReachabilityProbe = new Thread(asServiceReachabilityProbe);
            asReachabilityProbe.start();

            Thread rasReachabilityProbe = new Thread(rasServiceReachabilityProbe);
            rasReachabilityProbe.start();

            Thread msReachabilityProbe = new Thread(msServiceReachabilityProbe);
            msReachabilityProbe.start();

            Thread fnsReachabilityProbe = new Thread(fnsServiceReachabilityProbe);
            fnsReachabilityProbe.start();
        }
}
