package cloud.fogbow.probes.core.services;

import cloud.fogbow.probes.core.models.AuditableOrderStateChange;
import cloud.fogbow.probes.core.models.OrderState;
import cloud.fogbow.probes.datastore.DatabaseManager;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public int getOpened(Timestamp timestamp) {
        return dbManager.getEventsByTimeAndState(timestamp, OrderState.OPEN).size();
    }

    public List<Number> getLatencies(Timestamp timestamp) {
        Map<String, Pair<Timestamp, Timestamp>> ordersLatency = new HashMap<>();
        List<AuditableOrderStateChange> openEvents = dbManager.getEventsByTimeAndState(timestamp, OrderState.OPEN);
        List<AuditableOrderStateChange> fulfilledEvents = dbManager.getEventsByTimeAndState(timestamp, OrderState.FULFILLED);

        for(AuditableOrderStateChange stateChange: openEvents) {
            ordersLatency.put(stateChange.getOrder().getId(), new Pair(stateChange.getTimestamp(), null));
        }

        for(AuditableOrderStateChange stateChange: fulfilledEvents) {
            if(ordersLatency.containsKey(stateChange.getOrder().getId())) {
                Pair oldPair = ordersLatency.get(stateChange.getOrder().getId());
                ordersLatency.replace(stateChange.getOrder().getId(), new Pair(oldPair.getKey(), stateChange.getTimestamp()));
                continue;
            }

            AuditableOrderStateChange openEvent = dbManager.getEventByOrderAndState(stateChange.getOrder(), OrderState.OPEN);

            ordersLatency.put(stateChange.getOrder().getId(), new Pair<>(openEvent.getTimestamp(), stateChange.getTimestamp()));
        }

        List<Number> latencies = new ArrayList<>();

        for(String key: ordersLatency.keySet()) {
            Pair<Timestamp, Timestamp> current = ordersLatency.get(key);
            long latency = current.getValue().getTime() - current.getKey().getTime();
            latencies.add(latency);
        }

        return latencies;
    }
}
