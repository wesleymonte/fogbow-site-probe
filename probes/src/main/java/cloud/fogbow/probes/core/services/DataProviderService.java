package cloud.fogbow.probes.core.services;

import cloud.fogbow.probes.core.models.AuditableOrderStateChange;
import cloud.fogbow.probes.core.models.OrderState;
import cloud.fogbow.probes.core.models.ResourceType;
import cloud.fogbow.probes.datastore.DatabaseManager;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public List<AuditableOrderStateChange> getOpened(Timestamp timestamp, boolean firstTimeAwake, ResourceType type) {
        if(firstTimeAwake) {
            return getEventsOfType(dbManager.getEventsBeforeTimeAndState(timestamp, OrderState.OPEN), type);
        } else {
            return getEventsOfType(dbManager.getEventsAfterTimeAndState(timestamp, OrderState.OPEN), type);
        }
    }

    public List<AuditableOrderStateChange> getFailedOnRequest(Timestamp timestamp, boolean firstTimeAwake, ResourceType type) {
        if(firstTimeAwake) {
            return getEventsOfType(dbManager.getEventsBeforeTimeAndState(timestamp, OrderState.FAILED_ON_REQUEST), type);
        } else {
            return getEventsOfType(dbManager.getEventsAfterTimeAndState(timestamp, OrderState.FAILED_ON_REQUEST), type);
        }

    }

    public List<AuditableOrderStateChange> getFailed(Timestamp timestamp, boolean firstTimeAwake, ResourceType type) {
        if(firstTimeAwake) {
            return getEventsOfType(dbManager.getEventsBeforeTimeAndState(timestamp, OrderState.FAILED_AFTER_SUCCESSFUL_REQUEST), type);
        } else {
            return getEventsOfType(dbManager.getEventsAfterTimeAndState(timestamp, OrderState.FAILED_AFTER_SUCCESSFUL_REQUEST), type);
        }

    }

    public List<AuditableOrderStateChange> getFulfilled(Timestamp timestamp, boolean firstTimeAwake, ResourceType type) {
        if(firstTimeAwake) {
            return getEventsOfType(dbManager.getEventsBeforeTimeAndState(timestamp, OrderState.FULFILLED), type);
        } else {
            return getEventsOfType(dbManager.getEventsAfterTimeAndState(timestamp, OrderState.FULFILLED), type);
        }
    }

    public List<Pair<Number, Timestamp>>[] getLatencies(Timestamp timestamp, boolean firstTimeAwake) {
        List<Pair<Number, Timestamp>> result[] = new List[3];

        List<AuditableOrderStateChange> openEvents = getOpened(timestamp, firstTimeAwake);
        List<AuditableOrderStateChange> fulfilledEvents = getFulfilled(timestamp, firstTimeAwake);

        result[0] = computeLatencies(getEventsOfType(openEvents, ResourceType.COMPUTE), getEventsOfType(fulfilledEvents, ResourceType.COMPUTE));
        result[1] = computeLatencies(getEventsOfType(openEvents, ResourceType.VOLUME), getEventsOfType(fulfilledEvents, ResourceType.VOLUME));
        result[2] = computeLatencies(getEventsOfType(openEvents, ResourceType.NETWORK), getEventsOfType(fulfilledEvents, ResourceType.NETWORK));

        return result;
    }

    private List<Pair<Number, Timestamp>> computeLatencies(List<AuditableOrderStateChange> openEvents, List<AuditableOrderStateChange> fulfilledEvents) {
        Map<String, Pair<Timestamp, Timestamp>> ordersLatency = new HashMap<>();

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

        List<Pair<Number, Timestamp>> latencies = new ArrayList<>();

        for(String key: ordersLatency.keySet()) {
            Pair<Timestamp, Timestamp> current = ordersLatency.get(key);
            if(current.getKey() != null && current.getValue() != null ) {
                long latency = current.getValue().getTime() - current.getKey().getTime();
                latencies.add(new Pair(latency, current.getValue()));
            }
        }

        return latencies;
    }

    private List<AuditableOrderStateChange> getEventsOfType(List<AuditableOrderStateChange> events, ResourceType type) {
        return events.stream().filter(event -> event.getOrder().getType().equals(type)).collect(Collectors.toList());
    }
}
