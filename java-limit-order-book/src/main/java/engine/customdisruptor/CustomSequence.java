package engine.customdisruptor;

import java.util.concurrent.atomic.AtomicLong;

public class CustomSequence {
    // 56 bytes of padding before the value (7 longs * 8 bytes)
    protected long p1,p2,p3,p4,p5,p6,p7;

    // The actual sequence number tracker (aligned on its own cache line)
    private AtomicLong value;

    // 56 bytes of padding after the value (7 longs * 8 bytes)
    protected long p9,p10,p11,p12,p13,p14,p15;

    public CustomSequence() {}
    public CustomSequence(long value) {this.value = new AtomicLong(value);}

    public long get() {
        return value.get();
    }

    public long getAndIncrement(){
        return this.value.getAndIncrement();
    }

    public long incrementAndGet(){
        return this.value.incrementAndGet();
    }

    // Uses release semantics to make writes visible to other cores efficiently
    public void setVolatile(long value){
        this.value.set(value);
    }

    public boolean compareAndSet(long expect, long update) {
        return value.compareAndSet(expect, update);
    }
}
