import event.EventListener;
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

        MatchingEngine engine = new SingleThreadedMatchingEngine(eventListener);
//        EventRecorder recorder = new EventRecorder();
//        engine.setEventListener(recorder);

        WorkloadGenerator workload =
                new RandomWorkloadGenerator(seed, engine);

        long start = System.nanoTime();
        workload.generate(commandCount);
        long end = System.nanoTime();


        System.out.println("Commands processed: " + workload.getLastProcessedSequence());
//        System.out.println("Events produced: " + recorder.size());
        System.out.println("Elapsed ms: " + (end - start) / 1_000_000);

        InvariantChecker.check(engine);
//        InvariantChecker.check(recorder.events());
    }
}
