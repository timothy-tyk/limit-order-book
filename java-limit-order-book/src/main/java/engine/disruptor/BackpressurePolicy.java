package engine.disruptor;

public enum BackpressurePolicy {
    SPIN_RETRY,
    YIELD_RETRY,
    SLEEP
}

//                Thread.onSpinWait(); //spin and retry
//                Thread.yield(); // yield and retry
//                LockSupport.parkNanos(1_000); // sleep briefly