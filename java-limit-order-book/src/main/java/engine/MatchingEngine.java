package engine;

public interface MatchingEngine {
    void start();

    void stop();

    long lastProcessedSequence();
}
