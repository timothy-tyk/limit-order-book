package engine.customdisruptor;

import benchmark.LatencyRecorder;
import benchmark.WorkloadProfile;
import command.Command;
import core.OrderBook;
import engine.MatchingEngine;
import engine.SingleThreadedMatchingEngine;
import event.EventListener;
import utils.LiveOrderTracker;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.LockSupport;

public class BatchingConsumer implements Runnable, MatchingEngine {
    private final MiniDisruptor<ValueEvent> disruptor;
    private long nextSequence = 0L;
    private final SingleThreadedMatchingEngine delegate;

    public Thread consumerThread;

    private final AtomicLong submittedCommands;
    private final AtomicLong processedCommands;
    private final LongAdder publishRetries;

    private volatile boolean running;
    private volatile Throwable failure;

    private final EventListener eventListener;
    private final LatencyRecorder latencyRecorder;
    private final LiveOrderTracker liveOrderTracker;


    public BatchingConsumer(
            EventListener eventListener,
            LatencyRecorder latencyRecorder,
            LiveOrderTracker liveOrderTracker,
            int bufferSize,
            CustomWaitStrategy customWaitStrategy
    ) {
        this.disruptor = new MiniDisruptor<>(bufferSize,ValueEvent::new,customWaitStrategy);
        this.eventListener = eventListener;
        this.latencyRecorder = latencyRecorder;
        this.liveOrderTracker = liveOrderTracker;
        this.delegate = new SingleThreadedMatchingEngine(eventListener, latencyRecorder, liveOrderTracker);
        this.submittedCommands = new AtomicLong(0);
        this.processedCommands = new AtomicLong(0);
        this.publishRetries = new LongAdder();
    }

    @Override
    public void run() {
        CustomSequence producerCursor = disruptor.getProducerSequence();
        CustomWaitStrategy waitStrategy = disruptor.getCustomWaitStrategy();
        CustomSequence consumerCursor = disruptor.getConsumerSequence();

        try {
            while (!Thread.currentThread().isInterrupted()) {
                if (!disruptor.isShutdown()) {
                    // Wait for more data if not shutdown
                    waitStrategy.waitFor(nextSequence, producerCursor);
                }

                long availableSequence = producerCursor.get();

                // Process all available items
                for (long i = nextSequence; i <= availableSequence; i++) {
                    ValueEvent event = disruptor.getPreAllocated(i);
                    delegate.submitCommand(event.get());
                    processedCommands.incrementAndGet();
                }

                // Update cursor
                if (availableSequence >= nextSequence) {
                    consumerCursor.setVolatile(availableSequence);
                    nextSequence = availableSequence + 1L;
                }

                // Exit condition: shutdown AND all items processed
                if (disruptor.isShutdown() && nextSequence > producerCursor.get()) {
                    break;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void start() {
        if(running) return;
        running = true;
        consumerThread = new Thread(()->{
            run();
        });
        consumerThread.start();
    }

    public void stop(){
        running = false;
        if(consumerThread != null){
            consumerThread.interrupt();
        }

        delegate.stop();
        disruptor.shutdown();
    }

    @Override
    public long lastProcessedSequence() {
        return delegate.lastProcessedSequence();
    }

    @Override
    public void submitCommand(Command command) {
        long seq = disruptor.next();               // Reserve a slot (Handles Backpressure)
        ValueEvent event = disruptor.getPreAllocated(seq); // Fast Bitmask index lookup
        event.set(command);                         // Populate pre-allocated object
        disruptor.publish(seq);                    // Release to Consumer
        submittedCommands.incrementAndGet();
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

    public boolean awaitProcessed(long expectedCommands, long timeoutMilis){
        long deadline = System.currentTimeMillis()+timeoutMilis;
        while(processedCommands.get()<expectedCommands){
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

    public AtomicLong getSubmittedCommands() { return submittedCommands; }
    public AtomicLong getProcessedCommands() { return processedCommands; }
    public long getPublishRetries() { return publishRetries.sum(); }
}
