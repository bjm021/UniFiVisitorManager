package net.bjmsw.uvm.util;

import net.bjmsw.uvm.VisitorManager;
import net.bjmsw.uvm.model.Visitor;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Manages the periodic cleanup of expired visitors by leveraging a scheduled task.
 * The garbage collection process runs at a fixed interval and removes visitors whose remarks
 * match certain patterns ("UVM_EVENT_" or "UVM_ONETIME_") and are determined to be expired.
 * This ensures efficient memory and resource management by the system.
 */
public class GarbageCollector {

    private ScheduledExecutorService scheduler;
    private final int intervalMillis;

    public GarbageCollector(int intervalMillis) {
        this.intervalMillis = intervalMillis;
        System.out.println("[GC] Garbage Collector initialized with interval: " + intervalMillis + "ms");
    }

    public void start() {
        if (scheduler == null || scheduler.isShutdown()) {
            scheduler = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "UVM-GC-Thread"));

            scheduler.scheduleWithFixedDelay(this::runGarbageCollection, 0, intervalMillis, TimeUnit.MILLISECONDS);
            System.out.println("[GC] Garbage Collector started. Interval: " + intervalMillis + "ms");
        }
    }

    public void stop() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow(); // This safely interrupts the sleep and stops the task
            System.out.println("[GC] Garbage Collector stopped.");
        }
    }
    private void runGarbageCollection() {
        System.out.println("[GC] Starting garbage collection cycle...");
        try {
            List<Visitor> visitors = VisitorManager.getApiClient().getVisitors();

            int deletedCount = 0;
            for (Visitor v : visitors) {
                // ALWAYS check for null before calling .startsWith() to prevent NullPointerExceptions
                if (v.getRemarks() != null &&
                        (v.getRemarks().startsWith("UVM_EVENT_") || v.getRemarks().startsWith("UVM_ONETIME_"))) {

                    if (TimeUtils.isVisitorExpired(v)) {
                        VisitorManager.getApiClient().deleteVisitor(v.getId());
                        deletedCount++;
                        System.out.println("[GC] Deleted expired visitor: " + v.getEmail() + " (" + v.getRemarks() + ")");
                    }
                }
            }

            if (deletedCount > 0) {
                System.out.println("[GC] Cycle complete. Removed " + deletedCount + " expired visitors.");
            }

        } catch (Exception e) {
            System.err.println("[GC] Error during garbage collection cycle: " + e.getMessage());
            e.printStackTrace();
        }
    }
}