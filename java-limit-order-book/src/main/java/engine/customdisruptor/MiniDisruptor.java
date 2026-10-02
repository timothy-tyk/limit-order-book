package engine.customdisruptor;

import command.Command;

import java.util.function.Supplier;

public class MiniDisruptor<T> {
//    ring buffer
    private final T[] buffer;
    private final int bufferSize;
//    for fast bitwise ^2 calculations
    private final int mask;
    // claim / reserve cursor
    private final CustomSequence claimSequence = new CustomSequence(-1L);
//    Track producer's position
    private final CustomSequence producerSequence = new CustomSequence(-1L);
//    Track consumer's position
    private final CustomSequence consumerSequence = new CustomSequence(-1L);
    private final CustomWaitStrategy customWaitStrategy;

    private volatile boolean shutdown = false;

    /**
     *
     * •
     * claimSequence
     * - used by producers to reserve a unique slot
     * - protects against multiple producers taking the same slot
     * - uses CAS
     *
     * producerSequence
     * - tells the consumer the highest published slot
     * - only advances after payload is written and published
     *
     * consumerSequence
     * - tells the producer which slots have been processed
     * - used for backpressure / avoiding overwriting unread data
     */

    public MiniDisruptor(int size, Supplier<T> factory, CustomWaitStrategy waitStrategy){
        // Enforce Power of 2 constraint
        if ((size & (size - 1)) != 0) {
            throw new IllegalArgumentException("Buffer size must be a power of 2");
        }
        this.bufferSize = size;
        this.mask = size-1;
        this.customWaitStrategy = waitStrategy;
        this.buffer = (T[]) new Object[size];

//        Pre-allocate buffer to avoid allocation pressure
        for(int i=0;i<size;i++){
            buffer[i] = factory.get();
        }
    }

    public long next(){
        long current;
        long next;
//do-while loop : execute the code block once, before checking if the condition is true. Then it will repeat the loop as long as the condition is true.
        do{
            current = claimSequence.get();
            next = current+1L;
            long wrapPoint = next - bufferSize;
            while(wrapPoint>consumerSequence.get()){
                Thread.onSpinWait();
            }
        }while(!claimSequence.compareAndSet(current, next));
        // if compareAndSet == true, claimSequences advances, loop breaks
        // if false, Collision! Another producer thread beat this thread to the punch and advanced the claimSequence first. Because claimSequence is no longer equal to current, this thread fails safely, loops back to the top, reads the new updated sequence, and tries again.
        return next;
    }

    public T getPreAllocated(long sequence){
        return buffer[(int)(sequence&mask)];
    }

    public void publish(long sequence) {
        producerSequence.setVolatile(sequence);
        customWaitStrategy.signalAll();
    }

    public CustomSequence getProducerSequence() {
        return producerSequence;
    }

    public CustomSequence getConsumerSequence() {
        return consumerSequence;
    }

    public CustomWaitStrategy getCustomWaitStrategy() {
        return customWaitStrategy;
    }

    public void shutdown(){
        shutdown = true;
        customWaitStrategy.signalAll();
    }

    public boolean isShutdown(){
        return shutdown;
    }
}
