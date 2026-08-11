package command;

import core.Side;

public class ModifyOrderCommand implements Command{
    private long sequence;
    private long orderId;
    private Side side;
    private long newPrice;
    private long newQuantity;

    @Override
    public long sequence() {
        return sequence;
    }
}
