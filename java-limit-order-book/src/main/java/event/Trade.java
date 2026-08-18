package event;

public final class Trade implements Event{
    long eventSequence;
    long commandSequence;
    long buyOrderId;
    long sellOrderId;
    long price;
    long quantity;

    public Trade(long eventSequence, long commandSequence, long buyOrderId, long sellOrderId, long price, long quantity) {
        this.eventSequence = eventSequence;
        this.commandSequence = commandSequence;
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
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

    public long getBuyOrderId() {
        return buyOrderId;
    }

    public long getSellOrderId() {
        return sellOrderId;
    }

    public long getQuantity() {
        return quantity;
    }
}
