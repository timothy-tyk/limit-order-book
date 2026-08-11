import workload.RandomWorkloadGenerator;
import workload.WorkloadGenerator;
import engine.MatchingEngine;
import engine.SingleThreadedMatchingEngine;

public class Application {
    public static void main(String[] args) {
//        int commandCount = 1_000_000;
//        long seed = 42;
//
//        MatchingEngine engine = new SingleThreadedMatchingEngine();
//        EventRecorder recorder = new EventRecorder();
//        engine.setEventListener(recorder);
//
//        WorkloadGenerator workload =
//                new RandomWorkloadGenerator(commandCount, seed);
//
//        long start = System.nanoTime();
//        workload.run(engine);
//        long end = System.nanoTime();
//
//        InvariantChecker.check(engine);
//        InvariantChecker.check(recorder.events());
//
//        System.out.println("Commands processed: " + commandCount);
//        System.out.println("Events produced: " + recorder.size());
//        System.out.println("Elapsed ms: " + (end - start) / 1_000_000);
    }
}
