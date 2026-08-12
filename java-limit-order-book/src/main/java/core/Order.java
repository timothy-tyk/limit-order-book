package core;

public final class Order {
    private final long orderId;

    private final long symbolId;

    private final Side side;

    // use fixed-point integer, not double
    private final long price;

    // use long, not double
    private final long quantity;

    private long remainingQuantity;

    private final long timestamp;

    public Order(long orderId, long symbolId, Side side, long price, long quantity,long timestamp){
        this.orderId = orderId;
        this.symbolId = symbolId;
        this.side = side;
        this.price = price;
        this.quantity = quantity;
        this.remainingQuantity = quantity;
        this.timestamp = timestamp;
    }

    public long getOrderId() {
        return orderId;
    }

    public long getQuantity() {
        return quantity;
    }

    public long getRemainingQuantity() {
        return remainingQuantity;
    }

    public void setRemainingQuantity(long remainingQuantity) {
        this.remainingQuantity = remainingQuantity;
    }

    public long getPrice() {
        return price;
    }

    public Side getSide() {
        return side;
    }
}
