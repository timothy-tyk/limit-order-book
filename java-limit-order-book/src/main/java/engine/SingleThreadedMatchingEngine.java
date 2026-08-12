package engine;

import command.*;
import core.OrderBook;
import core.Side;

public class SingleThreadedMatchingEngine implements MatchingEngine {
    private long lastProcessedSequence = 0;
    private OrderBook orderBook;

    @Override
    public void start() {
//        Do nothing for single threaded engine
    }

    @Override
    public void stop() {
//        Do nothing for single threaded engine
    }

    @Override
    public long lastProcessedSequence() {
        return lastProcessedSequence;
    }

    public void submitCommand(Command command){
        // TODO: switch case for each command type
        switch(command){
            case AddLimitOrderCommand cmd -> addLimitOrder(cmd);
            case CancelOrderCommand cmd -> cancelLimitOrder(cmd);
            case ModifyOrderCommand cmd -> modifyLimitOrder(cmd);
            case MarketOrderCommand cmd -> marketLimitOrder(cmd);
            default -> throw new IllegalStateException("Unexpected value: " + command);
        }
        lastProcessedSequence = command.sequence();
    }

    void addLimitOrder(AddLimitOrderCommand cmd){
        /**
         * Given a new limit order:
         *
         * 1. Validate it.
         *    - price must be positive
         *    - quantity must be positive
         *    - order ID must not already exist
         *
         * 2. Try to match it immediately against the opposite side.
         *
         *    If incoming order is BUY:
         *        match against asks while:
         *            asks exist
         *            best ask price <= incoming buy price
         *            incoming quantity remains
         *
         *    If incoming order is SELL:
         *        match against bids while:
         *            bids exist
         *            best bid price >= incoming sell price
         *            incoming quantity remains
         *
         * 3. If quantity remains after matching:
         *        rest the order on the appropriate side.
         */
        if(cmd.validateCommand() && !orderBook.validateOrderExists(cmd.orderId)){
            if(cmd.side.equals(Side.BUY)){
                orderBook.matchBuyOrderOnAsks(cmd.price, cmd.quantity);
            }else{
                orderBook.matchSellOrderOnBids(cmd.price, cmd.quantity);
            }
        }
    }

    void cancelLimitOrder(CancelOrderCommand cmd){}
    void modifyLimitOrder(ModifyOrderCommand cmd){}
    void marketLimitOrder(MarketOrderCommand cmd){}
}
