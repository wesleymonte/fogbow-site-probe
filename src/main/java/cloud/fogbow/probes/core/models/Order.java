package cloud.fogbow.probes.core.models;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.Map;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "order_table")
public class Order {
    private static final long serialVersionUID = 1L;

    protected static final String REQUESTER_COLUMN_NAME = "requester";
    protected static final String PROVIDER_COLUMN_NAME = "provider";
    protected static final String CLOUD_NAME_COLUMN_NAME = "cloud_name";
    protected static final String INSTANCE_ID_COLUMN_NAME = "instance_id";

    public static final int FIELDS_MAX_SIZE = 255;
    public static final int ID_FIXED_SIZE = 36; // UUID size

    @Column
    @Id
    @Size(max = ID_FIXED_SIZE)
    private String id;

    @Column
    @Enumerated(EnumType.STRING)
    private OrderState orderState;

    @Column(name = REQUESTER_COLUMN_NAME)
    @Size(max = FIELDS_MAX_SIZE)
    private String requester;

    @Column(name = PROVIDER_COLUMN_NAME)
    @Size(max = FIELDS_MAX_SIZE)
    private String provider;

    @Column(name = CLOUD_NAME_COLUMN_NAME)
    @Size(max = FIELDS_MAX_SIZE)
    private String cloudName;

    @Column(name = INSTANCE_ID_COLUMN_NAME)
    @Size(max = FIELDS_MAX_SIZE)
    private String instanceId;

    @ElementCollection
    @MapKeyColumn
    @Column
    private Map<String, String> requirements = new HashMap<>();

    @Column
    @Size(max = FIELDS_MAX_SIZE)
    private String userId;

    @Column
    @Size(max = FIELDS_MAX_SIZE)
    private String providerId;

    @Column
    @Enumerated(EnumType.STRING)
    protected ResourceType type;

    public Order() {
    }

    public Order(String id) {
        this.id = id;
    }

    public Order(String id, String provider, String cloudName) {
        this(id);
        this.provider = provider;
        this.cloudName = cloudName;
    }

    public Order(String id, String provider, String cloudName, String requester) {
        this(id, provider, cloudName);
        this.requester = requester;
    }
}
