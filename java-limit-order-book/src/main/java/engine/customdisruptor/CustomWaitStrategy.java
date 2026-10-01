package engine.customdisruptor;

public interface CustomWaitStrategy {
    void waitFor(long targetSequence, CustomSequence cursor) throws InterruptedException;
    void signalAll();
}
