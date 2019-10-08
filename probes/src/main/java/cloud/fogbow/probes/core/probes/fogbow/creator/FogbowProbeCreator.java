package cloud.fogbow.probes.core.probes.fogbow.creator;

import cloud.fogbow.probes.core.probes.DefaultProbe;
import cloud.fogbow.probes.core.probes.MetricCollector;
import cloud.fogbow.probes.core.probes.Probe;
import cloud.fogbow.probes.core.probes.ProbeCreator;
import cloud.fogbow.probes.core.probes.fogbow.FogbowProbe;
import cloud.fogbow.probes.core.probes.fogbow.collectors.FogbowResourceAvailabilityMetricCollector;
import cloud.fogbow.probes.core.probes.fogbow.collectors.FogbowServiceLatencyMetricCollector;
import cloud.fogbow.probes.core.probes.fogbow.collectors.FogbowServiceReachabilityMetricCollector;
import cloud.fogbow.probes.core.probes.fogbow.collectors.FogbowServiceSuccessRateMetricCollector;
import cloud.fogbow.probes.provider.DataProviderService;
import java.util.Arrays;
import java.util.List;

public class FogbowProbeCreator implements ProbeCreator {

    private DataProviderService dataProviderService;

    public FogbowProbeCreator(DataProviderService dataProviderService) {
        this.dataProviderService = dataProviderService;
    }

    @Override
    public List<Probe> createProbes() {
        MetricCollector fogbowResourceAvailabilityMetricCollector = new FogbowResourceAvailabilityMetricCollector(dataProviderService);
        MetricCollector fogbowServiceLatencyMetricCollector = new FogbowServiceLatencyMetricCollector(dataProviderService);
        MetricCollector fogbowServiceSuccessRateMetricCollector = new FogbowServiceSuccessRateMetricCollector(dataProviderService);
        MetricCollector fogbowServiceReachabilityMetricCollector = new FogbowServiceReachabilityMetricCollector();

        Probe resourceAvailabilityProbe = new FogbowProbe(fogbowResourceAvailabilityMetricCollector, dataProviderService);
        Probe serviceLatencyProbe = new FogbowProbe(fogbowServiceLatencyMetricCollector, dataProviderService);
        Probe serviceSuccessRateProbe = new FogbowProbe(fogbowServiceSuccessRateMetricCollector, dataProviderService);
        Probe serviceReachabilityProbe = new DefaultProbe(fogbowServiceReachabilityMetricCollector);
        return Arrays.asList(resourceAvailabilityProbe, serviceLatencyProbe, serviceSuccessRateProbe, serviceReachabilityProbe);
    }


}
