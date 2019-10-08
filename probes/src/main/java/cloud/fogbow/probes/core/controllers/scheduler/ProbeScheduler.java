package cloud.fogbow.probes.core.controllers.scheduler;

import cloud.fogbow.probes.core.probes.Probe;
import java.util.List;

/**
 * The scheduler is responsible for managing the execution of a set of probes. For the scheduler to
 * run a probe it must be submitted.
 */
public interface ProbeScheduler {

    /**
     * The user can send Probes for the scheduler to execute according to their
     * scheduling policy.
     */
    void submitProbes(List<Probe> probes);
}
