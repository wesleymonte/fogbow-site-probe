package cloud.fogbow.probes.core.services;

import cloud.fogbow.probes.core.models.OrderState;
import cloud.fogbow.probes.datastore.DatabaseManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.sql.Timestamp;
import java.util.Random;

@Service
public class DataProviderService {

    @Autowired
    private DatabaseManager dbManager;

    public DataProviderService() {

    }

    public int getFailedOnRequest(Timestamp timestamp) {
        Random rand = new Random();
        return rand.nextInt((20 - 5) + 1) + 5;
    }

    public int getFulfilled(Timestamp timestamp) {
        Random rand = new Random();
        return rand.nextInt((20 - 5) + 1) + 5;
    }

    public int getFailed(Timestamp timestamp) {
        Random rand = new Random();
        return rand.nextInt((20 - 5) + 1) + 5;
    }
}
