package event;

import core.Side;

public final class OrderAccepted implements Event{
    long eventSequence;
    long commandSequence;
    long orderId;
    Side side;
    long price;
    long quantity;

    public OrderAccepted(long eventSequence, long commandSequence, long orderId, Side side, long price, long quantity) {
        this.eventSequence = eventSequence;
        this.commandSequence = commandSequence;
        this.orderId = orderId;
        this.side = side;
        this.price = price;
        this.quantity = quantity;
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
