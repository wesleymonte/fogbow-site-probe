package cloud.fogbow.probes.core.http;

import cloud.fogbow.probes.core.models.OrderState;
import cloud.fogbow.probes.core.models.ResourceType;
import cloud.fogbow.probes.core.services.DataProviderService;
import java.sql.Timestamp;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;

public class FmaConverter {

    @Autowired
    protected DataProviderService providerService;

    public Pair<String, String> getAuditFromResource(OrderState orderState, ResourceType type,
        Timestamp lastTimestampAwake, boolean firstTimeAwake) {
        Pair<String, String> pair;
        switch (orderState) {
            case FAILED_ON_REQUEST:
                pair = new Pair(type.getValue(), String.valueOf(providerService.getFailedOnRequest(lastTimestampAwake, firstTimeAwake, type).size()));
                break;
            case FAILED_AFTER_SUCCESSFUL_REQUEST:
                pair = new Pair(type.getValue(), String.valueOf(providerService.getFailed(lastTimestampAwake, firstTimeAwake, type).size()));
                break;
            case FULFILLED:
                pair = new Pair(type.getValue(), String.valueOf(providerService.getFulfilled(lastTimestampAwake, firstTimeAwake, type).size()));
                break;
            case OPEN:
                pair = new Pair(type.getValue(), String.valueOf(providerService.getOpened(lastTimestampAwake, firstTimeAwake, type).size()));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + orderState);
        }
        return pair;
    }
}
