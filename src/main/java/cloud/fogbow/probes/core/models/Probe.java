package cloud.fogbow.probes.core.models;

import cloud.fogbow.probes.core.Constants;
import cloud.fogbow.probes.core.services.DataProviderService;
import cloud.fogbow.probes.core.utils.PropertiesUtil;
import eu.atmosphere.tmaf.monitor.client.BackgroundClient;
import eu.atmosphere.tmaf.monitor.message.Data;
import eu.atmosphere.tmaf.monitor.message.Message;
import eu.atmosphere.tmaf.monitor.message.Observation;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
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
    protected void sendMessage(List<Integer> descriptionIds, Timestamp timestamp) {
        if(message == null) {
            createMessage();
        }

        for(int i = 1; i <= descriptionIds.size(); i++) {
            this.message.addData(new Data(
                    Data.Type.EVENT, // Event or measurement?
                    descriptionIds.get(i-1), // how could I pass a string identifier to this field?
                    new Observation(
                        timestamp.getTime(), i
                    )) // does it make sense?
            );
        }

        System.out.println(client.send(message));
    }
}
