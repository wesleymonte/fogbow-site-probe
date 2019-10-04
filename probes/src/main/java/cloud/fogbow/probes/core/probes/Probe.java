package cloud.fogbow.probes.core.probes;

import cloud.fogbow.probes.core.models.Metric;
import java.util.List;

public interface Probe extends Runnable {

    List<Metric> getMetrics();

}
