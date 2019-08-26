package cloud.fogbow.probes.core.models;

import cloud.fogbow.probes.core.Constants;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javafx.util.Pair;

public abstract class FogbowDataProbe extends Probe {
    protected int SLEEP_TIME;

    protected void sendResourceDataMessages(List<List<Pair<Number, Timestamp>>> computeData,
        List<List<Pair<Number, Timestamp>>> volumeData,
        List<List<Pair<Number, Timestamp>>> networkData) {

        Integer resourceId;
        if (hasData(computeData)) {
            resourceId = Integer.valueOf(properties.getProperty(Constants.COMPUTE_RESOURCE_ID));
            sendMessage(resourceId, computeData);
        }

        if (hasData(volumeData)) {
            resourceId = Integer.valueOf(properties.getProperty(Constants.VOLUME_RESOURCE_ID));
            sendMessage(resourceId, volumeData);
        }

        if (hasData(networkData)) {
            resourceId = Integer.valueOf(properties.getProperty(Constants.NETWORK_RESOURCE_ID));
            sendMessage(resourceId, networkData);
        }

        sleep(SLEEP_TIME);
    }

    protected List<List<Pair<Number, Timestamp>>> getData(OrderState[] states, ResourceType type,
        Timestamp currentTimestamp) {

        if (states.length > 2) {
            throw new IllegalArgumentException("States may be not greater than 2");
        }

        List<List<Pair<Number, Timestamp>>> results = new ArrayList<>();

        List<Pair<Number, Timestamp>> l1 = new ArrayList<>();
        List<Pair<Number, Timestamp>> l2 = new ArrayList<>();
        List<Pair<Number, Timestamp>> l3 = new ArrayList<>();

        l1.add(getDataPair(states[0], type, currentTimestamp));
        l2.add(getDataPair(states[1], type, currentTimestamp));
        l3.add(
            new Pair(currentTimestamp.getTime() - lastTimestampAwake.getTime(), currentTimestamp));

        results.add(l1);
        results.add(l2);
        results.add(l3);
        this.firstTimeAwake = false;
        return results;
    }

    private Pair<Number, Timestamp> getDataPair(OrderState orderState, ResourceType type,
        Timestamp currentTimestamp) {
        Pair<Number, Timestamp> pair;
        switch (orderState) {
            case FAILED_ON_REQUEST:
                pair = new Pair(
                    providerService.getFailedOnRequest(lastTimestampAwake, firstTimeAwake, type)
                        .size(), currentTimestamp);
                break;
            case FAILED_AFTER_SUCCESSFUL_REQUEST:
                pair = new Pair(
                    providerService.getFailed(lastTimestampAwake, firstTimeAwake, type).size(),
                    currentTimestamp);
                break;
            case FULFILLED:
                pair = new Pair(
                    providerService.getFulfilled(lastTimestampAwake, firstTimeAwake, type).size(),
                    currentTimestamp);
                break;
            case OPEN:
                pair = new Pair(
                    providerService.getOpened(lastTimestampAwake, firstTimeAwake, type).size(),
                    currentTimestamp);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + orderState);
        }
        return pair;
    }
}
