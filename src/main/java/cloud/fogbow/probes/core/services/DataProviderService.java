package cloud.fogbow.probes.core.services;

import cloud.fogbow.probes.core.models.OrderState;
import cloud.fogbow.probes.datastore.DatabaseManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class DataProviderService {

    @Autowired
    private DatabaseManager dbManager;

    public DataProviderService() {

    }

    public int getFailedOnRequest(Timestamp timestamp) {
        return dbManager.getEventsByTimeAndState(timestamp, OrderState.FAILED_ON_REQUEST).size();
    }

    public int getFulfilled(Timestamp timestamp) {
        return dbManager.getEventsByTimeAndState(timestamp, OrderState.FULFILLED).size();
    }

    public int getFailed(Timestamp timestamp) {
        return dbManager.getEventsByTimeAndState(timestamp, OrderState.FAILED_AFTER_SUCCESSUL_REQUEST).size();
    }
}
