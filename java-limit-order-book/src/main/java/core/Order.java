package core;

public final class Order {
    public final long orderId;

    private final long symbolId;

    private final Side side;

    // use fixed-point integer, not double
    private final long price;

    // use long, not double
    private final long quantity;

    private long remainingQuantity;

    private final long timestamp;
}
