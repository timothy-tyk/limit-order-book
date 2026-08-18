package event;

import core.Side;

public final class OrderModified implements Event{
    long eventSequence;
    long commandSequence;
    long orderId;
    Side newSide;
    long newPrice;
    long newQuantity;

    public OrderModified(long eventSequence, long commandSequence, long orderId, Side newSide, long newPrice, long newQuantity) {
        this.eventSequence = eventSequence;
        this.commandSequence = commandSequence;
        this.orderId = orderId;
        this.newSide = newSide;
        this.newPrice = newPrice;
        this.newQuantity = newQuantity;
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

    public long getNewQuantity() {
        return newQuantity;
    }
}
