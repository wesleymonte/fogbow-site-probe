package cloud.fogbow.probes.datastore;

import cloud.fogbow.probes.core.models.AuditableOrderStateChange;
import cloud.fogbow.probes.core.models.Order;
import cloud.fogbow.probes.core.models.OrderState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;

@Component
public class DatabaseManager {

    @Autowired
    AuditableOrderStateChangeRepository auditableOrderStateChangeRepository;

    public List<AuditableOrderStateChange> getEventsByTimeAndState(Timestamp timestamp, OrderState state) {
        return auditableOrderStateChangeRepository.findByTimestampGreaterThanEqualAndNewState(timestamp, state);
    }

    public AuditableOrderStateChange getEventByOrderAndState(Order order, OrderState state) {
        return auditableOrderStateChangeRepository.findByOrderAndNewState(order, state);
    }
}
