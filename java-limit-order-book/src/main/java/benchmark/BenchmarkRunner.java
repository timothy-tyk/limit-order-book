package benchmark;

import engine.MatchingEngine;
import engine.SingleThreadedMatchingEngine;
import event.EventListener;
import utils.LiveOrderTracker;
import validation.EventRecorder;
import workload.RandomWorkloadGenerator;
import workload.WorkloadGenerator;

import java.util.List;

public class BenchmarkRunner {
    static void main() {
        for(WorkloadProfile profile: List.of(
                new WorkloadProfile("ADD_ONLY",42,1_000_000,100,0,0,0),
                new WorkloadProfile("ADD_AND_MARKET",42,1_000_000,100,0,0,15),
                new WorkloadProfile("BALANCED", 42,1_000_000, 50,25,25,15),
                new WorkloadProfile("HIGH_ADD_LOW_CHURN",42,1_000_000,80,10,10,15),
                new WorkloadProfile("CANCEL_HEAVY",42,1_000_000,30,30,40,5)
        )){

            for(int warmup=0;warmup<3;warmup++){
                EventListener eventListener = new EventRecorder(false);
                LatencyRecorder latencyRecorder = new LatencyRecorder(1000);
                LiveOrderTracker tracker = new LiveOrderTracker();
                MatchingEngine matchingEngine = new SingleThreadedMatchingEngine(eventListener, latencyRecorder, tracker);
                WorkloadGenerator workloadGenerator = new RandomWorkloadGenerator(profile,matchingEngine);
                workloadGenerator.generate(false);
            }

            for(int measured=0;measured<1;measured++){
                EventListener eventListener = new EventRecorder(false);
                LatencyRecorder latencyRecorder = new LatencyRecorder(1000);
                LiveOrderTracker tracker = new LiveOrderTracker();
                MatchingEngine matchingEngine = new SingleThreadedMatchingEngine(eventListener, latencyRecorder, tracker);
                WorkloadGenerator workloadGenerator = new RandomWorkloadGenerator(profile,matchingEngine);
                long start = System.nanoTime();
                workloadGenerator.generate(true);
                long end = System.nanoTime();
//                matchingEngine.showProfileSummary(profile);
                System.out.println("Elapsed ms: " + (end - start) / 1_000_000);
                System.out.println(String.format("Throughput: %.2fM/sec",1000.0/((end-start)/1000000)));
//                matchingEngine.showLatencySummary();
//                matchingEngine.showEventSummary();
                System.out.println("================\n");
            }

        }
    }
}
