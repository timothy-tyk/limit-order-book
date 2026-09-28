package command;

import core.Side;

public class ModifyOrderCommand implements Command{
    private long sequence;
    private long timestamp;
    private long orderId;
    private Side side;
    private long newPrice;
    private long newQuantity;

    public ModifyOrderCommand(long sequence,long timestamp ,long orderId, Side side, long newPrice, long newQuantity) {
        this.sequence = sequence;
        this.timestamp = timestamp;
        this.orderId = orderId;
        this.side = side;
        this.newPrice = newPrice;
        this.newQuantity = newQuantity;
    }

    @Override
    public long sequence() {
        return sequence;
    }

    @Override
    public long timestamp() {
        return timestamp;
    }

    public long getOrderId() {
        return orderId;
    }

    public Side getSide() {
        return side;
    }

    public long getNewPrice() {
        return newPrice;
    }

    public long getNewQuantity() {
        return newQuantity;
    }
}
