import benchmark.LatencyRecorder;
import benchmark.WorkloadProfile;
import event.EventListener;
import utils.LiveOrderTracker;
import validation.EventRecorder;
import validation.InvariantChecker;
import workload.RandomWorkloadGenerator;
import workload.WorkloadGenerator;
import engine.MatchingEngine;
import engine.SingleThreadedMatchingEngine;

public class Application {
    public static void main(String[] args) {
        int commandCount = 1_000_000;
        long seed = 42;

        final boolean DONT_RETAIN_EVENTS = false;
        final boolean RETAIN_EVENTS = true;
        EventListener eventListener = new EventRecorder(DONT_RETAIN_EVENTS);
        LatencyRecorder latencyRecorder = new LatencyRecorder(1000);
        LiveOrderTracker tracker = new LiveOrderTracker();

        MatchingEngine engine = new SingleThreadedMatchingEngine(eventListener,latencyRecorder, tracker);
//        EventRecorder recorder = new EventRecorder();
//        engine.setEventListener(recorder);

        WorkloadProfile profile = new WorkloadProfile("NORMAL",42, 1_000_000, 70,20,10,15);
        WorkloadGenerator workload = new RandomWorkloadGenerator(profile, engine);

        long start = System.nanoTime();
        workload.generate(true);
        long end = System.nanoTime();


        System.out.println("Commands processed: " + workload.getLastProcessedSequence());
//        System.out.println("Events produced: " + recorder.size());
        System.out.println("Elapsed ms: " + (end - start) / 1_000_000);

        System.out.println(latencyRecorder.latencySummary());

        InvariantChecker.check(engine);
//        InvariantChecker.check(recorder.events());
    }
}
