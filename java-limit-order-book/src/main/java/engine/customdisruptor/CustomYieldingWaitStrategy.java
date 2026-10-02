package engine.customdisruptor;

public class CustomYieldingWaitStrategy implements CustomWaitStrategy{
    @Override
    public void waitFor(long targetSequence, CustomSequence cursor) throws InterruptedException {
        // Loop until the cursor reaches the target sequence
        while (cursor.get() < targetSequence) {
            // Yield to other threads instead of busy spinning
            Thread.yield();
        }
    }

    @Override
    public void signalAll() {

    }
}
