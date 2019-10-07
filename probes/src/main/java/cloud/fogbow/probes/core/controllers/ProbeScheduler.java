package cloud.fogbow.probes.core.controllers;

import cloud.fogbow.probes.core.probes.Probe;
import java.util.List;

public interface ProbeScheduler {
    void submitProbes(List<Probe> probes);
}
