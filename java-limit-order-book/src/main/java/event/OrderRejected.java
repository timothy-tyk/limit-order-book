package event;

import validation.OrderRejectedReason;

public final class OrderRejected implements Event{
    long eventSequence;
    long commandSequence;
    long orderId;
    OrderRejectedReason reason;

    public OrderRejected(long eventSequence, long commandSequence, long orderId, OrderRejectedReason reason) {
        this.eventSequence = eventSequence;
        this.commandSequence = commandSequence;
        this.orderId = orderId;
        this.reason = reason;
    }

    @Override
    public long eventSequence() {
        return eventSequence;
    }

    @Override
    public long commandSequence() {
        return commandSequence;
    }

    public OrderRejectedReason getReason() {
        return reason;
    }
}
