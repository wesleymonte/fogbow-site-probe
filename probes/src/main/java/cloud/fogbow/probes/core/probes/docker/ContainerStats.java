package cloud.fogbow.probes.core.probes.docker;

import org.json.JSONObject;

public class ContainerStats {

    private static final String CPU_STATS_KEY = "cpu_stats";
    private static final String MEMORY_STATS_KEY = "memory_stats";
    private CPUStats cpuStats;
    private MemoryStats memoryStats;

    public ContainerStats(JSONObject containerStats) {
        JSONObject cpuStats = containerStats.getJSONObject(CPU_STATS_KEY);
        JSONObject memoryStats = containerStats.getJSONObject(MEMORY_STATS_KEY);
        this.cpuStats = new CPUStats(cpuStats);
        this.memoryStats = new MemoryStats(memoryStats);
    }

    public Float calculateCPUPercentUnix(ContainerStats containerStats) {
        long previousCPU = containerStats.getCpuStats().getTotalUsage();
        long previousSystem = containerStats.getCpuStats().getSystemCPUUsage();
        float cpuPercent = this.calculateCPUPercentUnix(previousCPU, previousSystem);
        return cpuPercent;
    }

    private Float calculateCPUPercentUnix(long previousCPU, long previousSystem) {
        float cpuPercent = 0;
        long cpuDelta = this.getCpuStats().getTotalUsage() - previousCPU;
        long systemDelta = this.getCpuStats().getSystemCPUUsage() - previousSystem;
        long onlineCpus = this.getCpuStats().getOnlineCPUs();
        if (systemDelta > 0 && cpuDelta > 0) {
            cpuPercent = ((float) cpuDelta / (float) systemDelta) * onlineCpus * 100;
        }
        return cpuPercent;
    }

    public CPUStats getCpuStats() {
        return cpuStats;
    }

    public MemoryStats getMemoryStats() {
        return memoryStats;
    }

    private class CPUStats {

        private final String CPU_USAGE_KEY = "cpu_usage";
        private final String CPU_TOTAL_USAGE_KEY = "total_usage";
        private final String SYSTEM_CPU_USAGE_KEY = "system_cpu_usage";
        private final String ONLINE_CPUS_KEY = "online_cpus";
        private long totalUsage;
        private long systemCPUUsage;
        private long onlineCPUs;

        public CPUStats(JSONObject cpuStatsJson) {
            JSONObject cpuUsage = cpuStatsJson.getJSONObject(CPU_USAGE_KEY);
            long totalUsage = cpuUsage.getLong(CPU_TOTAL_USAGE_KEY);
            long systemCPUUsage = cpuStatsJson.getLong(SYSTEM_CPU_USAGE_KEY);
            long onlineCPUs = cpuStatsJson.getLong(ONLINE_CPUS_KEY);
            this.totalUsage = totalUsage;
            this.systemCPUUsage = systemCPUUsage;
            this.onlineCPUs = onlineCPUs;
        }

        public long getTotalUsage() {
            return totalUsage;
        }

        public long getSystemCPUUsage() {
            return systemCPUUsage;
        }

        public long getOnlineCPUs() {
            return onlineCPUs;
        }

    }

    public class MemoryStats {

        private final String USAGE_KEY = "usage";
        private long usage;

        public MemoryStats(JSONObject memoryStatsJson) {
            this.usage = memoryStatsJson.getLong(USAGE_KEY);
        }

        public long getUsage() {
            return usage;
        }
    }


}


