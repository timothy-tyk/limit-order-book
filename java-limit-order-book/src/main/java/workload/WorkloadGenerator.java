package workload;

import engine.MatchingEngine;

public interface WorkloadGenerator {
    void generate(boolean measuredRun);
    long getLastProcessedSequence();
}
