package command;

import core.Side;

public class AddLimitOrderCommand implements Command{
    long sequence;
    public long orderId;
    public Side side;
    public long price;
    public long quantity;

    public AddLimitOrderCommand(long sequence, long orderId, Side side, long price, long quantity) {
        this.sequence = sequence;
        this.orderId = orderId;
        this.side = side;
        this.price = price;
        this.quantity = quantity;
    }

    @Override
    public long sequence() {
        return sequence;
    }

    public boolean validateCommand(){
        return price>0 && quantity>0;
    }
}
