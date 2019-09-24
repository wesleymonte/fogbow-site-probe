package cloud.fogbow.probes.core.probes.docker;

import cloud.fogbow.probes.core.models.Metric;
import cloud.fogbow.probes.core.models.Probe;
import cloud.fogbow.probes.core.probes.docker.container.ContainerStats;
import cloud.fogbow.probes.core.probes.docker.container.DockerRequestHelper;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class DockerContainerProbe extends Probe {

    private static final String HELP = "Help";
    private static final Logger LOGGER = LogManager.getLogger(DockerContainerProbe.class);
    private Map<String, ContainerStats> previousContainersStats;
    private DockerRequestHelper dockerRequestHelper;

    public DockerContainerProbe(String dockerHostAddress, String ftaAddress) {
        super(dockerHostAddress, ftaAddress);
        this.previousContainersStats = new HashMap<>();
        this.dockerRequestHelper = new DockerRequestHelper(dockerHostAddress);
    }

    @Override
    protected List<Metric> getMetrics(Timestamp timestamp) {
        Map<String, ContainerStats> currentStats = new HashMap<>();
        List<Metric> metrics = new ArrayList<>();
        List<String> containerNames = dockerRequestHelper.listContainersName();
        for (String name : containerNames) {
            ContainerStats containerStats = getContainerStats(name);
            List<Metric> m = parseContainerStatsToMetrics(name, containerStats, timestamp);
            metrics.addAll(m);
            currentStats.put(name, containerStats);
        }
        previousContainersStats = currentStats;
        return metrics;
    }

    private ContainerStats getContainerStats(String containerName) {
        JSONObject stats = dockerRequestHelper.getContainerStats(containerName);
        ContainerStats containerStats = new ContainerStats(stats);
        return containerStats;
    }

    private List<Metric> parseContainerStatsToMetrics(String containerName,
        ContainerStats containerStats, Timestamp timestamp) {
        Metric memory = getMemoryMetric(containerName, containerStats, timestamp);
        Metric cpu = getCPUMetric(containerName, containerStats, timestamp);
        List<Metric> containerMetrics = new ArrayList<>(Arrays.asList(cpu, memory));
        return containerMetrics;
    }

    private Metric getCPUMetric(String containerName, ContainerStats stats, Timestamp timestamp) {
        float cpuPercent = 0;
        if (Objects.nonNull(previousContainersStats) && Objects
            .nonNull(previousContainersStats.get(containerName))) {
            ContainerStats previousStats = previousContainersStats.get(containerName);
            cpuPercent = stats.calculateCPUPercentUnix(previousStats);
        }
        Map<String, String> metadata = getDefaultMetadata(containerName);
        Metric metric = new Metric("cpu", cpuPercent, timestamp, HELP, metadata);
        return metric;
    }

    private Metric getMemoryMetric(String containerName, ContainerStats stats,
        Timestamp timestamp) {
        Long memoryUsage = stats.getMemoryStats().getUsage();
        Map<String, String> metadata = getDefaultMetadata(containerName);
        Metric metric = new Metric("memory", (float) memoryUsage, timestamp, HELP, metadata);
        return metric;
    }

    private Map<String, String> getDefaultMetadata(String containerName) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("resource", "container");
        metadata.put("name", containerName);
        return metadata;
    }


}
