package command;

public class CancelOrderCommand implements Command{
    private long sequence;
    private long orderId;

    public CancelOrderCommand(long sequence, long orderId) {
        this.sequence = sequence;
        this.orderId = orderId;
    }

    @Override
    public long sequence() {
        return sequence;
    }

    public long getOrderId() {
        return orderId;
    }
}
