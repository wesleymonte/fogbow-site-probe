package cloud.fogbow.probes;

import cloud.fogbow.probes.core.probes.FogbowResourceAvailabilityProbe;
import cloud.fogbow.probes.core.probes.FogbowServiceAvailabilityProbe;
import cloud.fogbow.probes.core.probes.FogbowServiceLatencyProbe;
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

        @PostConstruct
        public void startProbes() {
            Thread firstProbe = new Thread(resourceAvailabilityProbe);
            firstProbe.start();

            Thread secondProbe = new Thread(serviceLatencyProbe);
            secondProbe.start();

            Thread thirdProbe = new Thread(serviceAvailabilityProbe);
            thirdProbe.start();
        }
}
