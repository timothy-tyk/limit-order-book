package engine.concurrent;

import benchmark.LatencyRecorder;
import benchmark.WorkloadProfile;
import command.*;
import core.OrderBook;
import core.Side;
import engine.MatchingEngine;
import engine.SingleThreadedMatchingEngine;
import event.EventListener;
import utils.LiveOrderTracker;

import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public final class ReentrantLockMatchingEngine implements MatchingEngine, ConcurrentMatchingEngine {
    private final SingleThreadedMatchingEngine engine;
    private final ReentrantLock lock;

    public ReentrantLockMatchingEngine(EventListener eventListener, LatencyRecorder latencyRecorder, LiveOrderTracker liveOrderTracker) {
        engine = new SingleThreadedMatchingEngine(eventListener,latencyRecorder,liveOrderTracker);
        lock = new ReentrantLock();
    }

    @Override
    public void start() {
        lock.lock();
        try {
            engine.start();
        }finally {
            lock.unlock();
        }
    }

    @Override
    public void stop() {
        lock.lock();
        try{
            engine.stop();
        }finally{
            lock.unlock();
        }
    }

    @Override
    public long lastProcessedSequence() {
        lock.lock();
        try{
            return engine.lastProcessedSequence();
        }finally{
            lock.unlock();
        }
    }

    @Override
    public void submitCommand(Command command) {
        lock.lock();
        try{
            engine.submitCommand(command);
        }finally{
            lock.unlock();
        }
    }

    @Override
    public OrderBook getOrderBook() {
        lock.lock();
        try{
            return engine.getOrderBook();
        }finally{
            lock.unlock();
        }
    }

    @Override
    public LiveOrderTracker getLiveOrderTracker() {
        lock.lock();
        try{
            return engine.getLiveOrderTracker();
        }finally{
            lock.unlock();
        }
    }

    @Override
    public void showProfileSummary(WorkloadProfile profile) {
        lock.lock();
        try{
            engine.showProfileSummary(profile);
        }finally{
            lock.unlock();
        }
    }

    @Override
    public void showLatencySummary() {
        lock.lock();
        try{
            engine.showLatencySummary();
        }finally{
            lock.unlock();
        }
    }

    @Override
    public void showEventSummary() {
        lock.lock();
        try{
            engine.showEventSummary();
        }finally{
            lock.unlock();
        }
    }

    public void addLimitOrder(AddLimitOrderCommand command){
        lock.lock();
        try{
            engine.addLimitOrder(command);
        }finally{
            lock.unlock();
        }
    }

    public void cancelLimitOrder(CancelOrderCommand command){
        lock.lock();
        try{
            engine.cancelLimitOrder(command);
        }finally{
            lock.unlock();
        }
    }

    public void modifyLimitOrder(ModifyOrderCommand command){
        lock.lock();
        try{
            engine.modifyLimitOrder(command);
        }finally{
            lock.unlock();
        }
    }

    public void marketLimitOrder(MarketOrderCommand command){
        lock.lock();
        try{
            engine.marketLimitOrder(command);
        }finally{
            lock.unlock();
        }
    }

    @Override
    public void submitRandomCancel(long sequence, Random random) {
        lock.lock();
        try {
            if(!engine.getLiveOrderTracker().hasLiveOrders()) return;
            long orderIdToCancel = engine.getLiveOrderTracker().randomLiveOrderId(random);
            CancelOrderCommand cancelOrderCommand = new CancelOrderCommand(sequence, orderIdToCancel);
            engine.submitCommand(cancelOrderCommand);
        }finally{
            lock.unlock();
        }
    }

    @Override
    public void submitRandomModify(long sequence, Random random, long basePrice) {
        lock.lock();
        try{
            if(!engine.getLiveOrderTracker().hasLiveOrders()) return;
            long orderIdToModify = engine.getLiveOrderTracker().randomLiveOrderId(random);
            Side newSide = random.nextBoolean() ? Side.BUY : Side.SELL;
            long priceOffset = random.nextInt(20) - 10;
            long newPrice = basePrice + priceOffset;
            long newQty = random.nextInt(100) + 1;
            ModifyOrderCommand modifyOrderCommand = new ModifyOrderCommand(
                    sequence,
                    orderIdToModify,
                    newSide,
                    newPrice,
                    newQty
            );
            engine.submitCommand(modifyOrderCommand);
        }finally{
            lock.unlock();
        }
    }
}
