package cloud.fogbow.probes.core.probes;

import cloud.fogbow.probes.core.fta.FtaSender;
import cloud.fogbow.probes.core.models.Metric;
import java.sql.Timestamp;
import java.util.List;

/**
 * It is an entity in charge of making observations at every moment of time ). All observations are
 * sent to the Fogbow Telemetry Aggregator by address {@link #ftaAddress} using {@link FtaSender}.
 */
public interface Probe extends Runnable {

    List<Metric> getMetrics(Timestamp timestamp);

}
