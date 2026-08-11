package command;

public class CancelOrderCommand implements Command{
    private long sequence;
    private long orderId;

    @Override
    public long sequence() {
        return sequence;
    }
}
