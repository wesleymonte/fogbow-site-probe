package cloud.fogbow.probes.core.models;

import eu.atmosphere.tmaf.monitor.client.BackgroundClient;
import eu.atmosphere.tmaf.monitor.message.Data;
import eu.atmosphere.tmaf.monitor.message.Message;
import eu.atmosphere.tmaf.monitor.message.Observation;

import java.sql.Timestamp;
import java.util.List;

public abstract class Probe implements Runnable {

    private BackgroundClient client;
    private Message message;


    protected abstract void setup();
    protected abstract void createMessage();
    protected void sendMessage(List<Integer> descriptionIds, Timestamp timestamp) {
        if(message == null) {
            createMessage();
        }

        for(int i = 1; i <= descriptionIds.size(); i++) {
            this.message.addData(new Data(
                    Data.Type.MEASUREMENT,
                    descriptionIds.get(i),
                    new Observation(
                        timestamp.getTime(), i
                    ))
            );
        }

        client.send(message);
    }
}
