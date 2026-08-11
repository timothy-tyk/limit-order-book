package command;

import core.Side;

public class AddLimitOrderCommand implements Command{
    long sequence;
    public long orderId;
    public Side side;
    public long price;
    public long quantity;

    @Override
    public long sequence() {
        return sequence;
    }

    public boolean validateCommand(){
        return price>0 && quantity>0;
    }
}
