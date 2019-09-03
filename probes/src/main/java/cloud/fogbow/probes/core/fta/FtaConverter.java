package cloud.fogbow.probes.core.fta;

import cloud.fogbow.probes.core.models.Observation;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import cloud.fogbow.probes.core.utils.Pair;

public class FtaConverter {

    public static Observation createObservation(String label, List<Pair<String, Float>> values, Timestamp timestamp) throws IllegalArgumentException {
        if(Objects.isNull(label) || Objects.isNull(values) || Objects.isNull(timestamp) || values.isEmpty()){
            throw new IllegalArgumentException("Any argument to observation may be not null");
        }
        Observation observation = new Observation(label, values, timestamp);
        return observation;
    }
}
