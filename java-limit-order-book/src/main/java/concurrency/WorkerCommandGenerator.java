package concurrency;

import benchmark.WorkloadProfile;
import engine.MatchingEngine;
import utils.LiveOrderTracker;
import workload.WorkloadGenerator;

import java.util.Random;

public class WorkerCommandGenerator implements WorkloadGenerator {
    private final WorkloadProfile profile;
    private final MatchingEngine engine;
    private LiveOrderTracker liveOrderTracker;

    public WorkerCommandGenerator(WorkloadProfile profile, MatchingEngine engine) {
        this.profile = profile;
        this.engine = engine;
        this.liveOrderTracker = engine.getLiveOrderTracker();
    }

    @Override
    public void generate(boolean measuredRun) {
        Random random = new Random(profile.getSeed());
        long commandCount = profile.getCommandCount();

        long nextOrderId = 1;
        long sequence = 1;

        long basePrice = 100_000;
    }

    @Override
    public long getLastProcessedSequence() {
        return 0;
    }


}
