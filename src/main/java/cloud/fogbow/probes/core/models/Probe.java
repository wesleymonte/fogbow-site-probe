package cloud.fogbow.probes.core.models;

import cloud.fogbow.probes.core.services.DataProviderService;
import eu.atmosphere.tmaf.monitor.client.BackgroundClient;
import eu.atmosphere.tmaf.monitor.message.Data;
import eu.atmosphere.tmaf.monitor.message.Message;
import eu.atmosphere.tmaf.monitor.message.Observation;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Properties;

public abstract class Probe implements Runnable {

    protected static final int SLEEP_TIME = 60000;

    protected BackgroundClient client;
    protected Message message;
    protected Properties properties;
    protected Timestamp lastTimestampAwake;

    @Autowired
    protected DataProviderService providerService;

    protected abstract void setup() throws Exception;
    protected abstract void createMessage();
    private static int messageId = 0;
    protected void sendMessage(List<Integer> descriptionIds, Timestamp timestamp) {
        //if(message == null) {
            createMessage();
        //}

        //for(int i = 1; i <= descriptionIds.size(); i++) {
            this.message.setResourceId(1);
            this.message.setSentTime(Instant.now().getEpochSecond());
            this.message.setMessageId(messageId++);
            this.message.addData(new Data(
                    Data.Type.MEASUREMENT, // Event or measurement?
                    27, // how could I pass a string identifier to this field?
                    new Observation(
                            Instant.now().getEpochSecond(),
                            42.0
                    )) // does it make sense?
            );
        //}

        System.out.println(message);
        System.out.println(client.send(message));
    }
}
