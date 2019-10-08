package cloud.fogbow.probes.datastore;

import cloud.fogbow.probes.core.models.AuditableOrderStateChange;
import cloud.fogbow.probes.core.models.Order;
import cloud.fogbow.probes.core.models.OrderState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface AuditableOrderStateChangeRepository extends JpaRepository<AuditableOrderStateChange, Long> {

    List<AuditableOrderStateChange> findByTimestampGreaterThanEqualAndNewState(Timestamp timestamp, OrderState state);

    List<AuditableOrderStateChange> findByOrder_CloudNameAndTimestampGreaterThanEqualAndNewState(
        String cloudName, Timestamp timestamp, OrderState state);

    List<AuditableOrderStateChange> findByTimestampLessThanEqualAndNewState(Timestamp timestamp, OrderState state);

    AuditableOrderStateChange findByOrderAndNewState(Order order, OrderState state);

    List<AuditableOrderStateChange> findByTimestampGreaterThanEqual(Timestamp timestamp);

    @Query("SELECT MAX (timestamp) FROM AuditableOrderStateChange")
    Timestamp findMaxTimestamp();

}
