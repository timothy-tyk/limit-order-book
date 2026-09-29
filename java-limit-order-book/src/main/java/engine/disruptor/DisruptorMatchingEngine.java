package engine.disruptor;

import benchmark.LatencyRecorder;
import benchmark.WorkloadProfile;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import command.Command;
import core.OrderBook;
import engine.MatchingEngine;
import engine.SingleThreadedMatchingEngine;
import event.EventListener;
import utils.LiveOrderTracker;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;

public class DisruptorMatchingEngine implements MatchingEngine {
    private final Disruptor<CommandEvent> disruptor;
    private RingBuffer<CommandEvent> ringBuffer;
    private final SingleThreadedMatchingEngine delegate;
    private final AtomicLong submittedCommands;
    private final AtomicLong processedCommands;
    private volatile boolean running;
    private volatile Throwable failure;

    private final EventListener eventListener;
    private final LatencyRecorder latencyRecorder;
    private final LiveOrderTracker liveOrderTracker;

    public DisruptorMatchingEngine(EventListener eventListener,
                                   LatencyRecorder latencyRecorder,
                                   LiveOrderTracker liveOrderTracker,
                                   int bufferSize,
                                   WaitStrategy waitStrategy
    ){
        this.eventListener = eventListener;
        this.latencyRecorder = latencyRecorder;
        this.liveOrderTracker = liveOrderTracker;
        this.delegate = new SingleThreadedMatchingEngine(eventListener, latencyRecorder, liveOrderTracker);
        this.submittedCommands = new AtomicLong(0);
        this.processedCommands = new AtomicLong(0);

        ThreadFactory threadFactory = runnable-> new Thread(runnable, "disruptor-engine");
        this.disruptor = new Disruptor<>(CommandEvent::new, bufferSize,threadFactory,ProducerType.MULTI, waitStrategy);
        this.disruptor.handleEventsWith(this::onEvent);
        };

    @Override
    public void start() {
        if(running) return;
        delegate.start();
        this.ringBuffer = disruptor.start();
        running = true;
    }

    @Override
    public void stop() {
        running = false;
        disruptor.shutdown();
        delegate.stop();
    }

    public void onEvent(CommandEvent event, long sequence, boolean endOfBatch){
        try{
            delegate.submitCommand(event.command);
            processedCommands.incrementAndGet();
        }catch (Throwable t){
            failure = t;
        }finally {
            event.clear();
        }
    }

    @Override
    public long lastProcessedSequence() {
        return delegate.lastProcessedSequence();
    }

    @Override
    public void submitCommand(Command command) {
        if(!running) {
            throw new IllegalStateException("Engine is not running!");
        }
            long createdAtNanos = command.timestamp();
            while (!tryPublish(command, createdAtNanos)) {
                Thread.onSpinWait();
            }
            submittedCommands.incrementAndGet();
    }

    private boolean tryPublish(Command command, long createdAtNanos){
            return ringBuffer.tryPublishEvent((event, sequence) -> {
                event.command = command;
                event.createdAtNanos = createdAtNanos;
            });
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

    public AtomicLong getSubmittedCommands() { return submittedCommands; }
    public AtomicLong getProcessedCommands() { return processedCommands; }

}
