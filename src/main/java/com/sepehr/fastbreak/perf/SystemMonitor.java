package com.sepehr.fastbreak.perf;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadMXBean;
import java.util.List;

public final class SystemMonitor {

    private static final com.sun.management.OperatingSystemMXBean OS =
            ManagementFactory.getPlatformMXBean(com.sun.management.OperatingSystemMXBean.class);
    private static final MemoryMXBean MEMORY = ManagementFactory.getMemoryMXBean();
    private static final List<GarbageCollectorMXBean> GC_BEANS =
            ManagementFactory.getGarbageCollectorMXBeans();
    private static final ThreadMXBean THREADS = ManagementFactory.getThreadMXBean();

    private SystemMonitor() {
    }

    public static long usedHeapBytes() {
        return MEMORY.getHeapMemoryUsage().getUsed();
    }

    public static long maxHeapBytes() {
        return MEMORY.getHeapMemoryUsage().getMax();
    }

    public static double processCpuLoad() {
        double load = OS.getProcessCpuLoad();
        return load < 0.0 ? -1.0 : load;
    }

    public static long totalGcCount() {
        long sum = 0L;
        for (GarbageCollectorMXBean bean : GC_BEANS) {
            long count = bean.getCollectionCount();
            if (count >= 0) {
                sum += count;
            }
        }
        return sum;
    }

    public static long totalGcTimeMs() {
        long sum = 0L;
        for (GarbageCollectorMXBean bean : GC_BEANS) {
            long time = bean.getCollectionTime();
            if (time >= 0) {
                sum += time;
            }
        }
        return sum;
    }

    public static int threadCount() {
        return THREADS.getThreadCount();
    }
}
