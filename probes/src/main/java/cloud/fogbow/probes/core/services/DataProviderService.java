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

    public List<AuditableOrderStateChange> getFailedOnRequest(Timestamp timestamp, boolean firstTimeAwake) {
        if(firstTimeAwake) {
            return dbManager.getEventsBeforeTimeAndState(timestamp, OrderState.FAILED_ON_REQUEST);
        } else {
            return dbManager.getEventsAfterTimeAndState(timestamp, OrderState.FAILED_ON_REQUEST);
        }

    }

    public List<AuditableOrderStateChange> getFulfilled(Timestamp timestamp, boolean firstTimeAwake) {
        if(firstTimeAwake) {
            return dbManager.getEventsBeforeTimeAndState(timestamp, OrderState.FULFILLED);
        } else {
            return dbManager.getEventsAfterTimeAndState(timestamp, OrderState.FULFILLED);
        }
    }

    public List<AuditableOrderStateChange> getFailed(Timestamp timestamp, boolean firstTimeAwake) {
        if(firstTimeAwake) {
            return dbManager.getEventsBeforeTimeAndState(timestamp, OrderState.FAILED_AFTER_SUCCESSFUL_REQUEST);
        } else {
            return dbManager.getEventsAfterTimeAndState(timestamp, OrderState.FAILED_AFTER_SUCCESSFUL_REQUEST);
        }

    }

    public List<AuditableOrderStateChange> getOpened(Timestamp timestamp, boolean firstTimeAwake) {
        if(firstTimeAwake) {
            return dbManager.getEventsBeforeTimeAndState(timestamp, OrderState.OPEN);
        } else {
            return dbManager.getEventsAfterTimeAndState(timestamp, OrderState.OPEN);
        }
    }

    public List<Number> getLatencies(Timestamp timestamp, boolean firstTimeAwake) {
        Map<String, Pair<Timestamp, Timestamp>> ordersLatency = new HashMap<>();
        List<AuditableOrderStateChange> openEvents = getOpened(timestamp, firstTimeAwake);
        List<AuditableOrderStateChange> fulfilledEvents = getFulfilled(timestamp, firstTimeAwake);

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
