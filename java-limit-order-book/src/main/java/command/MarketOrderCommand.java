package command;

import core.Side;

public class MarketOrderCommand implements Command{
    private long sequence;
    private long orderId;
    private Side side;
    private long quantity;

    @Override
    public long sequence() {
        return sequence;
    }
}
