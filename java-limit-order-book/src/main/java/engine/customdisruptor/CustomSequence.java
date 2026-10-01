package engine.customdisruptor;

public class CustomSequence {
    // 56 bytes of padding before the value (7 longs * 8 bytes)
    protected long p1,p2,p3,p4,p5,p6,p7;

    // The actual sequence number tracker (aligned on its own cache line)
    protected volatile long value = -1L;

    // 56 bytes of padding after the value (7 longs * 8 bytes)
    protected long p9,p10,p11,p12,p13,p14,p15;

    public CustomSequence() {}
    public CustomSequence(long value) {this.value = value;}

    public long get() {
        return value;
    }

    public void set(long value) {
        this.value = value;
    }

    public void setVolatile(long value){
        this.value = value;
    }
}
