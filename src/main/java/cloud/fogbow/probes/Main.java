package cloud.fogbow.probes;

import cloud.fogbow.probes.core.models.FogbowResourceAvailabilityProbe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class Main implements ApplicationRunner {

        @Override
        public void run(ApplicationArguments args) {
//            FogbowResourceAvailabilityProbe serviceResourceProbe = new FogbowResourceAvailabilityProbe();
//            System.out.println(serviceResourceProbe.getProviderService());
//            Thread firstProbe = new Thread(serviceResourceProbe);
//            firstProbe.start();

        }

        @Autowired
        FogbowResourceAvailabilityProbe serviceResourceProbe;

        @PostConstruct
        public void tst() {
            Thread firstProbe = new Thread(serviceResourceProbe);
            firstProbe.start();
        }
}
