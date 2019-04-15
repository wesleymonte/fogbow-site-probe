package cloud.fogbow.probes;

import cloud.fogbow.probes.core.models.FogbowResourceAvailabilityProbe;

public class Main {
        public static void main(String[] args) throws Exception{
            FogbowResourceAvailabilityProbe serviceResourceProbe = new FogbowResourceAvailabilityProbe();
            Thread firstProbe = new Thread(serviceResourceProbe);
            firstProbe.start();
            firstProbe.join();
        }
}
