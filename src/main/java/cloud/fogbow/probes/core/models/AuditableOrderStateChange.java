package cloud.fogbow.probes.core.models;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "state_change_event")
public class AuditableOrderStateChange {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Order order;

    @Column
    private Timestamp timestamp;

    @Column
    @Enumerated(EnumType.STRING)
    private OrderState newState;

    @Column
    @Enumerated(EnumType.STRING)
    private ResourceType resourceType;

    public AuditableOrderStateChange() {
    }

    public AuditableOrderStateChange(Timestamp timestamp, Order order, OrderState newState) {
        this.order = order;
        this.timestamp = timestamp;
        this.newState = newState;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
