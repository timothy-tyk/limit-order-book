package command;

public class CancelOrderCommand implements Command{
    private long sequence;
    private long timestamp;
    private long orderId;

    public CancelOrderCommand(long sequence, long timestamp,long orderId) {
        this.sequence = sequence;
        this.timestamp = timestamp;
        this.orderId = orderId;
    }

    @Override
    public long sequence() {
        return sequence;
    }

    @Override
    public long timestamp() {
        return 0;
    }

    public long getOrderId() {
        return orderId;
    }
}
