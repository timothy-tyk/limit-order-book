package engine;

import benchmark.LatencyRecorder;
import benchmark.WorkloadProfile;
import command.Command;
import core.OrderBook;
import utils.LiveOrderTracker;

public interface MatchingEngine {
    void start();

    void stop();

    long lastProcessedSequence();

    void submitCommand(Command command);

    OrderBook getOrderBook();

    LiveOrderTracker getLiveOrderTracker();

    void showProfileSummary(WorkloadProfile profile);
    void showLatencySummary();

    void showEventSummary();
}
