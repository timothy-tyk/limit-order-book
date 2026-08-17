package engine;

import benchmark.WorkloadProfile;
import command.Command;
import core.OrderBook;

public interface MatchingEngine {
    void start();

    void stop();

    long lastProcessedSequence();

    void submitCommand(Command command);

    OrderBook getOrderBook();

    void showEventSummary(WorkloadProfile profile);
}
