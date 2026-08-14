package event;

public final class OrderRejected implements Event{
    long eventSequence;
    long commandSequence;
    long orderId;

    public OrderRejected(long eventSequence, long commandSequence, long orderId) {
        this.eventSequence = eventSequence;
        this.commandSequence = commandSequence;
        this.orderId = orderId;
    }

    @Override
    public long eventSequence() {
        return eventSequence;
    }

    @Override
    public long commandSequence() {
        return commandSequence;
    }
}
