package cloud.fogbow.probes;

import cloud.fogbow.probes.core.models.FogbowResourceAvailabilityProbe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class Main implements ApplicationRunner {

        @Override
        public void run(ApplicationArguments args) {
            FogbowResourceAvailabilityProbe serviceResourceProbe = new FogbowResourceAvailabilityProbe();
            Thread firstProbe = new Thread(serviceResourceProbe);
            firstProbe.start();

        }
}
