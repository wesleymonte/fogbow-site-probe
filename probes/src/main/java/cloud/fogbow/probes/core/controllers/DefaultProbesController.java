package cloud.fogbow.probes.core.controllers;

import cloud.fogbow.probes.core.probes.Probe;
import cloud.fogbow.probes.core.probes.ProbeCreator;
import cloud.fogbow.probes.core.probes.docker.DockerProbeCreator;
import java.util.List;

public class DefaultProbesController {

    private ProbeScheduler probeScheduler;
    private ProbeCreator probeCreator;

    public DefaultProbesController() {
        this.probeScheduler = new DefaultProbeScheduler();
        this.probeCreator = new DockerProbeCreator();
    }

    public void submitProbes() {
        List<Probe> probes = this.probeCreator.createProbes();
        this.probeScheduler.submitProbes(probes);
    }

    public void setProbeCreator(ProbeCreator probeCreator) {
        this.probeCreator = probeCreator;
    }
}
