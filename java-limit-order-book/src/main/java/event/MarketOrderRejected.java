package event;

import core.Side;
import validation.OrderRejectedReason;

public final class MarketOrderRejected implements Event{
    long eventSequence;
    long commandSequence;
    long orderId;
    OrderRejectedReason reason;

    public MarketOrderRejected(long eventSequence, long commandSequence, long orderId, OrderRejectedReason reason) {
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
}
