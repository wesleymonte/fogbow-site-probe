package cloud.fogbow.probes.core.fta;

import cloud.fogbow.probes.core.models.Metric;
import cloud.fogbow.probes.core.models.Value;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import cloud.fogbow.probes.core.utils.Pair;

public class FtaConverter {

    public static Metric createObservation(String name, List<Pair<String, Float>> values, Timestamp timestamp, String help) throws IllegalArgumentException {
        if(Objects.isNull(name) || Objects.isNull(values) || Objects.isNull(timestamp) || values.isEmpty()){
            throw new IllegalArgumentException("Any argument to metric may be not null");
        }
        List<Value> valuesList = toValueList(values);
        Metric metric = new Metric(name, valuesList, timestamp, help);
        return metric;
    }

    private static List<Value> toValueList(List<Pair<String, Float>> values){
        List<Value> out = new ArrayList<>();
        for(Pair<String, Float> p : values){
            Value v = new Value(p.getKey(), p.getValue());
            out.add(v);
        }
        return out;
    }
}
