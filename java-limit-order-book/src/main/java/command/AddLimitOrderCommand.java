package command;

import core.Side;

public class AddLimitOrderCommand implements Command{
    private long sequence;
    private long timestamp;
    private long orderId;
    private Side side;
    private long price;
    private long quantity;

    public AddLimitOrderCommand(long sequence, long timestamp, long orderId, Side side, long price, long quantity) {
        this.sequence = sequence;
        this.timestamp = timestamp;
        this.orderId = orderId;
        this.side = side;
        this.price = price;
        this.quantity = quantity;
    }

    @Override
    public long sequence() {
        return sequence;
    }

    @Override
    public long timestamp() {
        return timestamp;
    }

    public boolean validateCommand(){
        return price>0 && quantity>0;
    }

    public long getOrderId() {
        return orderId;
    }

    public Side getSide() {
        return side;
    }

    public long getPrice() {
        return price;
    }

    public long getQuantity() {
        return quantity;
    }
}
