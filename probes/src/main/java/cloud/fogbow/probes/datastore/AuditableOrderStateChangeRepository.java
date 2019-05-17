package cloud.fogbow.probes.datastore;

import cloud.fogbow.probes.core.models.AuditableOrderStateChange;
import cloud.fogbow.probes.core.models.Order;
import cloud.fogbow.probes.core.models.OrderState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface AuditableOrderStateChangeRepository extends JpaRepository<AuditableOrderStateChange, Long> {

    List<AuditableOrderStateChange> findByTimestampGreaterThanEqualAndNewState(Timestamp timestamp, OrderState state);

    AuditableOrderStateChange findByOrderAndNewState(Order order, OrderState state);
}
