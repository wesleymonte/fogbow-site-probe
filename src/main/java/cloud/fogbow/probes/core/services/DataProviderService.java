package cloud.fogbow.probes.core.services;

import java.sql.Timestamp;
import java.util.Random;
public class DataProviderService {

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
