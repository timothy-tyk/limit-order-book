package event;

import core.Side;

public final class OrderCancelled implements Event{
    long eventSequence;
    long commandSequence;
    long orderId;

    public OrderCancelled(long eventSequence, long commandSequence, long orderId) {
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

    public long getOrderId() {
        return orderId;
    }
}
