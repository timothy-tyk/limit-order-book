package workload;

import engine.MatchingEngine;

public interface WorkloadGenerator {
    long generate();
    long getLastProcessedSequence();
}
