package cloud.fogbow.probes.core.fta;

import cloud.fogbow.probes.core.models.Metric;
import cloud.fogbow.probes.core.models.Observation;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import cloud.fogbow.probes.core.utils.Pair;

public class FtaConverter {

    public static Metric createMetric(String name, List<Pair<String, Float>> observation, Timestamp timestamp, String help) throws IllegalArgumentException {
        if(Objects.isNull(name) || Objects.isNull(observation) || Objects.isNull(timestamp) || observation.isEmpty()){
            throw new IllegalArgumentException("Any argument to metric may be not null");
        }
        List<Observation> valuesList = toObservationList(observation);
        Metric metric = new Metric(name, valuesList, timestamp, help);
        return metric;
    }

    private static List<Observation> toObservationList(List<Pair<String, Float>> values){
        List<Observation> out = new ArrayList<>();
        for(Pair<String, Float> p : values){
            Observation v = new Observation(p.getKey(), p.getValue());
            out.add(v);
        }
        return out;
    }
}
