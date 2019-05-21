package cloud.fogbow.probes.core.models;

import cloud.fogbow.probes.core.services.DataProviderService;
import eu.atmosphere.tmaf.monitor.client.BackgroundClient;
import eu.atmosphere.tmaf.monitor.message.Data;
import eu.atmosphere.tmaf.monitor.message.Message;
import eu.atmosphere.tmaf.monitor.message.Observation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;

public abstract class Probe implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Probe.class);

    protected static final int SLEEP_TIME = 60000;

    protected BackgroundClient client;
    protected Message message;
    protected Properties properties;
    protected Timestamp lastTimestampAwake;
    protected Integer resourceId;
    protected Integer probeId;
    protected boolean firstTimeAwake;

    @Autowired
    protected DataProviderService providerService;

    private static int messageId = 0;

    protected void setup() {
        this.client = new BackgroundClient();
        client.authenticate(probeId, "pass".getBytes());
        boolean startFlag = client.start();

        if(!startFlag) {
            System.out.println("failed on starting");
        }
    }

    protected void sendMessage(List<Number> dataValues) {
        createMessage();

        this.message.setResourceId(resourceId);
        this.message.setMessageId(messageId++);

        int descriptionId = 0;

        for(Number number : dataValues) {
            this.message.addData(new Data(
                Data.Type.MEASUREMENT,
                descriptionId++,
                new Observation(
                    lastTimestampAwake.getTime(),
                    number.doubleValue()
                ))
            );
        }

        client.send(message);
    }

    private void createMessage() {
        this.message = this.client.createMessage();
    }
}
