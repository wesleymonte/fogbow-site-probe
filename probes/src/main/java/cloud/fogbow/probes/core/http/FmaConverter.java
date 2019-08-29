package cloud.fogbow.probes.core.http;

import cloud.fogbow.probes.core.models.Observation;
import cloud.fogbow.probes.core.models.OrderState;
import cloud.fogbow.probes.core.models.ResourceType;
import cloud.fogbow.probes.core.services.DataProviderService;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;

public class FmaConverter {

    public static Observation createObservation(String label, List<Pair<String, Float>> values, Timestamp timestamp){
        if(Objects.isNull(label) || Objects.isNull(values) || Objects.isNull(timestamp)){
            throw new IllegalArgumentException("Any argument may be not null");
        }
        Observation observation = new Observation(label, values, timestamp);
        return observation;
    }
}
