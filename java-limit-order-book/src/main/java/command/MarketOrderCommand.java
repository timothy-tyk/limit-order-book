package command;

import core.Side;

public class MarketOrderCommand implements Command{
    private long sequence;
    private long timestamp;
    private long orderId;
    private Side side;
    private long quantity;


    public MarketOrderCommand(long sequence, long timestamp, long orderId, Side side, long quantity) {
        this.sequence = sequence;
        this.timestamp = timestamp;
        this.orderId = orderId;
        this.side = side;
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
        return quantity>0;
    }

    public long getQuantity() {
        return quantity;
    }

    public Side getSide() {
        return side;
    }

    public long getOrderId() {
        return orderId;
    }
}
