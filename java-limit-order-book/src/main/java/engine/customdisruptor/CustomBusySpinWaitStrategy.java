package engine.customdisruptor;

public class CustomBusySpinWaitStrategy implements CustomWaitStrategy{

    @Override
    public void waitFor(long targetSequence, CustomSequence cursor) throws InterruptedException {
//        Loop relentlessly until the data is available
//        Thread.onSpinWait() hints to the CPU that this is a spin-loop, optimizing energy/execution
        while(cursor.get()<targetSequence){
            Thread.onSpinWait();
        }
    }

    @Override
    public void signalAll() {
//      Nothing to do, thread is always spinning
    }
}
