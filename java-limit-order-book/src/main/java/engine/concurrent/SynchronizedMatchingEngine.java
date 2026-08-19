package engine.concurrent;

import benchmark.LatencyRecorder;
import benchmark.WorkloadProfile;
import command.*;
import core.OrderBook;
import engine.MatchingEngine;
import engine.SingleThreadedMatchingEngine;
import event.EventListener;
import utils.LiveOrderTracker;

public final class SynchronizedMatchingEngine implements MatchingEngine {
    private final SingleThreadedMatchingEngine engine;

    public SynchronizedMatchingEngine(EventListener eventListener, LatencyRecorder latencyRecorder, LiveOrderTracker liveOrderTracker) {
        this.engine = new SingleThreadedMatchingEngine(eventListener,latencyRecorder,liveOrderTracker);
    }

    @Override
    public synchronized void start() {
        engine.start();
    }

    @Override
    public synchronized void stop() {
        engine.stop();
    }

    @Override
    public synchronized long lastProcessedSequence() {
        return engine.lastProcessedSequence();
    }

    @Override
    public synchronized void submitCommand(Command command) {
        engine.submitCommand(command);
    }

    @Override
    public synchronized OrderBook getOrderBook() {
        return engine.getOrderBook();
    }

    @Override
    public synchronized LiveOrderTracker getLiveOrderTracker() {
        return engine.getLiveOrderTracker();
    }

    @Override
    public synchronized void showProfileSummary(WorkloadProfile profile) {
        engine.showProfileSummary(profile);
    }

    @Override
    public synchronized void showLatencySummary() {
        engine.showLatencySummary();
    }

    @Override
    public synchronized void showEventSummary() {
        engine.showEventSummary();
    }

    public synchronized void addLimitOrder(AddLimitOrderCommand command){
        engine.addLimitOrder(command);
    }

    public synchronized void cancelLimitOrder(CancelOrderCommand command){
        engine.cancelLimitOrder(command);
    }

    public synchronized void modifyLimitOrder(ModifyOrderCommand command){
        engine.modifyLimitOrder(command);
    }

    public synchronized void marketLimitOrder(MarketOrderCommand command){
        engine.marketLimitOrder(command);
    }
}
