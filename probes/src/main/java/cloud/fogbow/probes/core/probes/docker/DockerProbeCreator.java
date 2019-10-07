package cloud.fogbow.probes.core.probes.docker;

import cloud.fogbow.probes.core.probes.DefaultProbe;
import cloud.fogbow.probes.core.probes.Probe;
import cloud.fogbow.probes.core.probes.ProbeCreator;
import cloud.fogbow.probes.core.probes.docker.collectors.DockerContainerMetricCollector;
import java.util.Arrays;
import java.util.List;

public class DockerProbeCreator implements ProbeCreator {

    @Override
    public List<Probe> createProbes() {
        DockerContainerMetricCollector dockerContainerMetricCollector = new DockerContainerMetricCollector();
        DefaultProbe probe = new DefaultProbe(dockerContainerMetricCollector);
        return Arrays.asList(probe);
    }
}
