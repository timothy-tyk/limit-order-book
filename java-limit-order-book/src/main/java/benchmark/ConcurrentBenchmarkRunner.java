package benchmark;

import concurrency.ConcurrentWorkloadRunner;
import validation.InvariantChecker;

import java.util.List;

public class ConcurrentBenchmarkRunner {
    public static void main() throws InterruptedException {
        List<WorkloadProfile> workloadProfiles = List.of(
                new WorkloadProfile("MT_ADD_ONLY", 42, 1_000_000, 100, 0, 0, 0),
                new WorkloadProfile("MT_ADD_AND_MARKET", 42, 1_000_000, 100, 0, 0, 15),
                new WorkloadProfile("MT_ADD_THEN_CANCEL", 42, 1_000_000, 50, 25, 25, 15),
                new WorkloadProfile("MT_THREAD_LOCAL_CHURN", 42, 1_000_000, 80, 10, 10, 15)
        );
        ConcurrentWorkloadRunner concurrentWorkloadRunner = new ConcurrentWorkloadRunner();

        for (WorkloadProfile profile : workloadProfiles) {
            System.out.printf("=== %s | %s | %s ===\n", profile.getName(), profile.getSeed(), profile.getCommandCount());
            int[] threadCounts = {1, 2, 4, 8};
            for (int threadCount : threadCounts) {
                concurrentWorkloadRunner.runConcurrentWorkload(threadCount, profile);
            }
        }
    }
}

