package cloud.fogbow.probes.core.services;

import cloud.fogbow.probes.core.models.AuditableOrderStateChange;
import cloud.fogbow.probes.core.models.OrderState;
import cloud.fogbow.probes.core.models.ResourceType;
import cloud.fogbow.probes.datastore.DatabaseManager;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DataProviderService {

    @Autowired
    private DatabaseManager dbManager;

    private static final Logger LOGGER = LogManager.getLogger(DataProviderService.class);

    public DataProviderService() {

    }

    public List<AuditableOrderStateChange> getFailedOnRequest(Timestamp timestamp) {
        return dbManager.getEventsAfterTimeAndState(timestamp, OrderState.FAILED_ON_REQUEST);
    }

    public List<AuditableOrderStateChange> getFulfilled(Timestamp timestamp) {
        return dbManager.getEventsAfterTimeAndState(timestamp, OrderState.FULFILLED);
    }

    public List<AuditableOrderStateChange> getFailed(Timestamp timestamp) {
        return dbManager.getEventsAfterTimeAndState(timestamp, OrderState.FAILED_AFTER_SUCCESSFUL_REQUEST);
    }

    public List<AuditableOrderStateChange> getOpened(Timestamp timestamp) {
        return dbManager.getEventsAfterTimeAndState(timestamp, OrderState.OPEN);
    }

    public List<AuditableOrderStateChange> getOpened(Timestamp timestamp, ResourceType type) {
        return getEventsOfType(dbManager.getEventsAfterTimeAndState(timestamp, OrderState.OPEN), type);
    }

    public List<AuditableOrderStateChange> getFailedOnRequest(Timestamp timestamp, ResourceType type) {
        return getEventsOfType(dbManager.getEventsAfterTimeAndState(timestamp, OrderState.FAILED_ON_REQUEST), type);
    }

    public List<AuditableOrderStateChange> getFailed(Timestamp timestamp, ResourceType type) {
        return getEventsOfType(dbManager.getEventsAfterTimeAndState(timestamp, OrderState.FAILED_AFTER_SUCCESSFUL_REQUEST), type);
    }

    public List<AuditableOrderStateChange> getFulfilled(Timestamp timestamp, ResourceType type) {
        return getEventsOfType(dbManager.getEventsAfterTimeAndState(timestamp, OrderState.FULFILLED), type);
    }

    public Timestamp getMaxTimestampFromAuditOrders(){
        return dbManager.getMaxTimestamp();
    }

    public Long[] getLatencies(Timestamp timestamp) {
        List<AuditableOrderStateChange> fulfilledEvents = getFulfilled(timestamp);

        Long computeLatency = calculateLatency(getEventsOfType(fulfilledEvents, ResourceType.COMPUTE));
        Long networkLatency = calculateLatency(getEventsOfType(fulfilledEvents, ResourceType.NETWORK));
        Long volumeLatency = calculateLatency(getEventsOfType(fulfilledEvents, ResourceType.VOLUME));

        Long[] latencies = {computeLatency, networkLatency, volumeLatency};

        return latencies;
    }

    public Integer getAuditsFromResourceByState(OrderState orderState, ResourceType type,
        Timestamp lastTimestampAwake) {
        Integer value;
        switch (orderState) {
            case FAILED_ON_REQUEST:
                value = getFailedOnRequest(lastTimestampAwake, type).size();
                break;
            case FAILED_AFTER_SUCCESSFUL_REQUEST:
                value = getFailed(lastTimestampAwake, type).size();
                break;
            case FULFILLED:
                value = getFulfilled(lastTimestampAwake, type).size();
                break;
            case OPEN:
                value = getOpened(lastTimestampAwake, type).size();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + orderState);
        }
        LOGGER.info("Got audits of resource type [" + type.getValue() + "] from state [" + orderState.name() + "] with result value [" + value + "]");
        return value;
    }

    private Long calculateLatency(List<AuditableOrderStateChange> fulfilledEvents){
        List<Long> latencies = new ArrayList<>();
        for(AuditableOrderStateChange aosc : fulfilledEvents){
            AuditableOrderStateChange auditableOrderStateChangeOpen = dbManager.getEventByOrderAndState(aosc.getOrder(), OrderState.OPEN);
            if(!Objects.isNull(auditableOrderStateChangeOpen)){
                Long latency = aosc.getTimestamp().getTime() - auditableOrderStateChangeOpen.getTimestamp().getTime();
                latencies.add(latency);
            }
        }
        return average(latencies);
    }

    private AuditableOrderStateChange getEventWithOrder(List<AuditableOrderStateChange> events, String orderId){
        AuditableOrderStateChange auditableOrderStateChange = null;
        for(AuditableOrderStateChange aosc : events){
            if(aosc.getOrder().getId().equals(orderId)){
                auditableOrderStateChange = aosc;
                break;
            }
        }
        return auditableOrderStateChange;
    }

    private Long average(List<Long> list){
        double sum = 0;
        for(Long l : list){
            sum += l;
        }
        double average = sum / list.size();
        return (long) average;
    }

    private List<AuditableOrderStateChange> getEventsOfType(List<AuditableOrderStateChange> events, ResourceType type) {
        return events.stream().filter(event -> event.getOrder().getType().equals(type)).collect(Collectors.toList());
    }
}
