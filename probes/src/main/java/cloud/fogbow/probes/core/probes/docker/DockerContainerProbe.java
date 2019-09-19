package cloud.fogbow.probes.core.probes.docker;

import cloud.fogbow.probes.core.models.Metric;
import cloud.fogbow.probes.core.models.Probe;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class DockerContainerProbe extends Probe {

    public static final String THREAD_NAME = "Thread-Docker-Container-Probe";
    private static final String PROBE_NAME = "docker_container_information";
    private static final String HELP = "Help";
    private static final Logger LOGGER = LogManager
        .getLogger(DockerContainerProbe.class);
    private DockerRequestHelper dockerRequestHelper = new DockerRequestHelper();

    public DockerContainerProbe(Integer sleepTime, String ftaAddress) {
        super(sleepTime, ftaAddress);
    }

    @Override
    protected List<Metric> getMetrics(Timestamp timestamp) {
        List<Metric> metrics = new ArrayList<>();
        List<String> ids = dockerRequestHelper.listContainersName();
        for(String id : ids){
            List<Metric> m = getContainerMetrics(id, timestamp);
            metrics.addAll(m);
        }
        return metrics;
    }

    private List<Metric> getContainerMetrics(String containerName, Timestamp timestamp){
        List<Metric> containerMetrics = new ArrayList<>();
        JSONObject stats = dockerRequestHelper.getContainerStats(containerName);
        Metric memory = getMemoryMetric(containerName, stats, timestamp);
        Metric cpu = getCPUMetric(containerName, stats, timestamp);
        containerMetrics.add(memory);
        containerMetrics.add(cpu);
        return containerMetrics;
    }

    private Metric getCPUMetric(String containerName, JSONObject stats, Timestamp timestamp){
        Float cpuPercent = calculateCPUPercentUnix(0, 0, stats);
        Map<String, String> metadata = getDefaultMetadata(containerName);
        Metric metric = new Metric("cpu", cpuPercent, timestamp, HELP, metadata);
        return metric;
    }

    private Float calculateCPUPercentUnix(long previousCPU, long previousSystem, JSONObject stats){
        float cpuPercent = 0;
        long cpuDelta = getCPUTotalUsage(stats) - previousCPU;
        long systemDelta = getSystemCPUUsage(stats) - previousSystem;
        int onlineCpus = getOnlineCPUs(stats);
        if(systemDelta > 0 && cpuDelta > 0){
            cpuPercent = ((float)cpuDelta / (float)systemDelta) * onlineCpus * 100;
        }
        return cpuPercent;
    }

    private Long getCPUTotalUsage(JSONObject stats){
        String cpuStatsKey = "cpu_stats";
        String cpuUsageKey = "cpu_usage";
        String cpuTotalUsageKey = "total_usage";
        JSONObject cpuStats = stats.getJSONObject(cpuStatsKey);
        JSONObject cpuUsage = cpuStats.getJSONObject(cpuUsageKey);
        Long cpuTotalUsage = cpuUsage.getLong(cpuTotalUsageKey);
        return cpuTotalUsage;
    }

    private Long getSystemCPUUsage(JSONObject stats){
        String cpuStatsKey = "cpu_stats";
        String systemCpuUsageKey = "system_cpu_usage";
        JSONObject cpuStats = stats.getJSONObject(cpuStatsKey);
        Long systemCpuUsage = cpuStats.getLong(systemCpuUsageKey);
        return systemCpuUsage;
    }

    private int getOnlineCPUs(JSONObject stats){
        String cpuStatsKey = "cpu_stats";
        String onlineCpusKey = "online_cpus";
        JSONObject cpuStats = stats.getJSONObject(cpuStatsKey);
        int onlineCpus = cpuStats.getInt(onlineCpusKey);
        return onlineCpus;
    }

    private Metric getMemoryMetric(String containerName, JSONObject stats, Timestamp timestamp){
        String memoryStatsKey = "memory_stats";
        String memoryUsageKey = "usage";
        JSONObject memoryStats = stats.getJSONObject(memoryStatsKey);
        Long memoryUsage = memoryStats.getLong(memoryUsageKey);
        Map<String, String> metadata = getDefaultMetadata(containerName);
        Metric metric = new Metric("memory", (float) memoryUsage, timestamp, HELP, metadata);
        return metric;
    }

    private Map<String, String> getDefaultMetadata(String containerName){
        Map<String, String> metadata = new HashMap<>();
        metadata.put("resource", "container");
        metadata.put("name", containerName);
        return metadata;
    }


}
