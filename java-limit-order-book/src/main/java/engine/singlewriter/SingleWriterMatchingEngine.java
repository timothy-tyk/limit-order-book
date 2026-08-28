package engine.singlewriter;

import benchmark.LatencyRecorder;
import benchmark.WorkloadProfile;
import command.Command;
import core.OrderBook;
import engine.MatchingEngine;
import engine.SingleThreadedMatchingEngine;
import event.EventListener;
import utils.LiveOrderTracker;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;

/**
 * serialize ownership
 * send work to the owner thread
 * let the owner thread process it deterministically
 */

public class SingleWriterMatchingEngine implements MatchingEngine {
    private final BlockingQueue<Command> queue;
    private final SingleThreadedMatchingEngine delegate;
    private final AtomicLong submittedCommands;
    private final AtomicLong processedCommands;
    private volatile boolean running;
    private volatile Throwable failure;
    private Thread workerThread;

    private final EventListener eventListener;
    private final LatencyRecorder latencyRecorder;
    private final LiveOrderTracker liveOrderTracker;

    public SingleWriterMatchingEngine(EventListener eventListener, LatencyRecorder latencyRecorder, LiveOrderTracker liveOrderTracker, int queueCapacity) {
        this.eventListener = eventListener;
        this.latencyRecorder = new LatencyRecorder(10000);
        this.liveOrderTracker = new LiveOrderTracker();
        this.queue = new ArrayBlockingQueue<>(queueCapacity);
        this.delegate = new SingleThreadedMatchingEngine(eventListener, latencyRecorder, liveOrderTracker);
        submittedCommands = new AtomicLong(0);
        processedCommands = new AtomicLong(0);
    }

    @Override
    public void start() {
        if (running) return;
        running = true;
        delegate.start();
        workerThread = new Thread(() -> {
            runLoop();
        }, "single-writer-engine");
        workerThread.start();
    }

    @Override
    public void stop() {
        running = false;
        if (workerThread != null) {
            try{
                workerThread.join();
            }catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }
        }
        delegate.stop();

    }

    private void runLoop() {
        try {
            while (running || !queue.isEmpty()) {
                Command command = queue.poll(10, TimeUnit.MILLISECONDS);
                if (command != null) {
                    delegate.submitCommand(command);
                    processedCommands.incrementAndGet();
                } else {
                    running = false;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Throwable t) {
            failure = t;
        }
    }


    @Override
    public long lastProcessedSequence() {
        return delegate.lastProcessedSequence();
    }

    @Override
    public void submitCommand(Command command) {
        if (!running) {
            throw new IllegalStateException("Engine is not running!");
        }
        try {
            queue.put(command);
            submittedCommands.incrementAndGet();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Thread is interrupted while submitting command.");
        }
    }

    public boolean awaitQueueCompletion(long timeoutMilis){
        long deadline = System.currentTimeMillis()+timeoutMilis;
        while(processedCommands.get()<submittedCommands.get()){
            if(failure!=null){
                throw new IllegalStateException("Engine failed: "+failure);
            }
            if(System.currentTimeMillis()>deadline){
                return false;
            }
            Thread.onSpinWait();
            LockSupport.parkNanos(100_000);
        }
        return true;
    }

    @Override
    public OrderBook getOrderBook() {
        return delegate.getOrderBook();
    }

    @Override
    public LiveOrderTracker getLiveOrderTracker() {
        return liveOrderTracker;
    }

    @Override
    public void showProfileSummary(WorkloadProfile profile) {

    }

    @Override
    public void showLatencySummary() {
        System.out.println(latencyRecorder.latencySummary());
    }

    @Override
    public void showEventSummary() {
        System.out.println(eventListener.summary());
    }

    public AtomicLong getSubmittedCommands() {
        return submittedCommands;
    }

    public AtomicLong getProcessedCommands() {
        return processedCommands;
    }
}


