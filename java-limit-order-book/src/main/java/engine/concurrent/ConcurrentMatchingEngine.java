package engine.concurrent;

import engine.MatchingEngine;

import java.util.Random;

public interface ConcurrentMatchingEngine extends MatchingEngine {
    void submitRandomCancel(long sequence, Random random);
    void submitRandomModify(long sequence, Random random, long basePrice);
}
