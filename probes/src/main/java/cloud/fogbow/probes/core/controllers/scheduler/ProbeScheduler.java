package cloud.fogbow.probes.core.controllers.scheduler;

import cloud.fogbow.probes.core.probes.Probe;
import java.util.List;

public interface ProbeScheduler {
    void submitProbes(List<Probe> probes);
}
