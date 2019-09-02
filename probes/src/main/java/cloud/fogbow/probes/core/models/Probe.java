package cloud.fogbow.probes.core.models;

import cloud.fogbow.probes.core.services.DataProviderService;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.util.Properties;

public abstract class Probe implements Runnable {

    @Autowired
    protected Properties properties;

    protected Timestamp lastTimestampAwake;
    protected Integer probeId;
    protected boolean firstTimeAwake;
    @Autowired
    protected DataProviderService providerService;

    protected Integer SLEEP_TIME;
    protected String FMA_ADDRESS;

    protected void sleep(int sleepTime) {
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
