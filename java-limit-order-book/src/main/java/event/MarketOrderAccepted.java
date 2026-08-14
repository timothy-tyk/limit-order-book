package event;

import core.Side;

public final class MarketOrderAccepted implements Event{
    long eventSequence;
    long commandSequence;
    long orderId;
    Side side;
    long quantity;

    public MarketOrderAccepted(long eventSequence, long commandSequence, long orderId, Side side, long quantity) {
        this.eventSequence = eventSequence;
        this.commandSequence = commandSequence;
        this.orderId = orderId;
        this.side = side;
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
