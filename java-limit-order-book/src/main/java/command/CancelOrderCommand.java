package command;

public class CancelOrderCommand implements Command{
    private long sequence;
    private long timestamp;
    private long orderId;

    public CancelOrderCommand(long sequence, long orderId) {
        this.sequence = sequence;
        this.timestamp = System.nanoTime();
        this.orderId = orderId;
    }

    @Override
    public long sequence() {
        return sequence;
    }

    @Override
    public long timestamp() {
        return timestamp;
    }

    public long getOrderId() {
        return orderId;
    }
}
