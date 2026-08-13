package engine;

import command.Command;

public interface MatchingEngine {
    void start();

    void stop();

    long lastProcessedSequence();

    void submitCommand(Command command);
}
