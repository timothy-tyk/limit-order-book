package engine.disruptor;

import com.lmax.disruptor.WaitStrategy;

public class DisruptorConfig {
    public int queueCapacity;
    public WaitStrategy waitStrategy;
    public BackpressurePolicy backPressurePolicy;

    public DisruptorConfig(int queueCapacity, WaitStrategy waitStrategy, BackpressurePolicy backPressurePolicy) {
        this.queueCapacity = queueCapacity;
        this.waitStrategy = waitStrategy;
        this.backPressurePolicy = backPressurePolicy;
    }
}

// High performance, high CPU. Spins aggressively first, then gives hints to the OS to let other threads run if they need to.
// consumer thread lets other threads use the CPU core while constantly (busy spin) checking if ringbuffer is populated
//        WaitStrategy waitStrategy = new YieldingWaitStrategy();

// Lowest CPU, highest latency. Puts the thread to a deep sleep; producer must actively sound an alarm (signal) to wake it up.
// consumer thread sleeps until producer thread populates ringbuffer and signals to wake up
//        WaitStrategy waitStrategy = new BlockingWaitStrategy();

// Balanced CPU and latency. Spins, then yields, and finally takes tiny, timed micro-naps to check the ring buffer without needing a producer signal.
// consumer thread sleeps and intermittently wakes up to check if ringbuffer is populated
//        WaitStrategy waitStrategy = new SleepingWaitStrategy();

// Maximum performance, maximum CPU. Never sleeps, never pauses; loops at 100% core utilization staring at the ring buffer.
// consumer thread never sleeps, always checking when ringbuffer is populated
//        WaitStrategy waitStrategy = new BusySpinWaitStrategy();
