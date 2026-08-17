package benchmark;

import engine.MatchingEngine;
import engine.SingleThreadedMatchingEngine;
import event.EventListener;
import validation.EventRecorder;
import workload.RandomWorkloadGenerator;
import workload.WorkloadGenerator;

import java.util.List;

public class BenchmarkRunner {
    static void main() {
        for(WorkloadProfile profile: List.of(
                new WorkloadProfile("ADD_ONLY",42,1_000_000,100,0,0,15),
                new WorkloadProfile("BALANCED", 42,1_000_000, 50,25,25,15),
                new WorkloadProfile("HIGH_ADD_LOW_CHURN",42,1_000_000,80,10,10,15)
        )){
            EventListener eventListener = new EventRecorder(false);
            LatencyRecorder latencyRecorder = new LatencyRecorder(1000);
            MatchingEngine matchingEngine = new SingleThreadedMatchingEngine(eventListener, latencyRecorder);
            WorkloadGenerator workloadGenerator = new RandomWorkloadGenerator(profile,matchingEngine);

            long start = System.nanoTime();
            workloadGenerator.generate();
            long end = System.nanoTime();

            System.out.println("Commands processed: " + workloadGenerator.getLastProcessedSequence());
//        System.out.println("Events produced: " + recorder.size());
            System.out.println("Elapsed ms: " + (end - start) / 1_000_000);
            System.out.println("===\n");
        }
    }
}
