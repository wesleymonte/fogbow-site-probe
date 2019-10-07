package cloud.fogbow.probes.core.probes;

import cloud.fogbow.probes.core.PropertiesHolder;
import cloud.fogbow.probes.core.fta.FtaSender;
import cloud.fogbow.probes.core.models.Metric;
import java.sql.Timestamp;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DefaultProbe implements Probe {

    private static final Logger LOGGER = LogManager.getLogger(DefaultProbe.class);
    private MetricCollector metricCollector;

    public DefaultProbe(MetricCollector metricCollector) {
        this.metricCollector = metricCollector;
    }

    @Override
    public void run() {
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        try {
            List<Metric> metrics = metricCollector.collect(currentTimestamp);
            FtaSender.sendMetrics(PropertiesHolder.getInstance().getFtaAddressProperty(), metrics);
        } catch (Exception e) {
            LOGGER.error(
                "Error while probe running at [" + currentTimestamp + "]: " + e.getMessage());
        }
    }
}
