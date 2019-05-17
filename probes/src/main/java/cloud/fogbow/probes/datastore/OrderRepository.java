package cloud.fogbow.probes.datastore;

import cloud.fogbow.probes.core.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, String> {
}
