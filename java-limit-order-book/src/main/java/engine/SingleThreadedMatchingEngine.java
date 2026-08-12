package engine;

import command.*;
import core.OrderBook;
import core.Side;

public class SingleThreadedMatchingEngine implements MatchingEngine {
    private long lastProcessedSequence = 0;
    private OrderBook orderBook;

    public SingleThreadedMatchingEngine() {
        this.orderBook = new OrderBook();
    }

    public OrderBook getOrderBook() {
        return orderBook;
    }

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

    public long addLimitOrder(AddLimitOrderCommand cmd){
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
        long remainingRequestQty=cmd.quantity;
        if(cmd.validateCommand() && !orderBook.validateOrderExists(cmd.orderId)){
            if(cmd.side.equals(Side.BUY)){
                remainingRequestQty = orderBook.matchBuyOrderOnAsks(cmd.price, cmd.quantity);
            }else{
                remainingRequestQty = orderBook.matchSellOrderOnBids(cmd.price, cmd.quantity);
            }
        }
        return remainingRequestQty;
    }

    void cancelLimitOrder(CancelOrderCommand cmd){
        /**
         * Given an order ID:
         *
         * 1. Look up the order in ordersById.
         * 2. If it does not exist, ignore or reject.
         * 3. If it exists:
         *    - remove it from its price level queue
         *    - if that price level becomes empty, remove the price level
         *    - remove it from ordersById
         */
        long orderIdToCancel = cmd.getOrderId();
        if(orderBook.validateOrderExists(orderIdToCancel)){
            orderBook.cancelOrder(orderIdToCancel);
        }
    }
    void modifyLimitOrder(ModifyOrderCommand cmd){}
    void marketLimitOrder(MarketOrderCommand cmd){}
}
