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

    @Override
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
        long remainingRequestQty=cmd.getQuantity();
        if(cmd.validateCommand() && !orderBook.validateOrderExists(cmd.getOrderId())){
            if(cmd.getSide().equals(Side.BUY)){
                remainingRequestQty = orderBook.matchBuyOrderOnAsks(cmd.getPrice(), cmd.getQuantity(), cmd.getOrderId());
            }else{
                remainingRequestQty = orderBook.matchSellOrderOnBids(cmd.getPrice(), cmd.getQuantity(),cmd.getOrderId());
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
    void modifyLimitOrder(ModifyOrderCommand cmd){
        /**
         * 1. Find existing order by ID.
         * 2. Cancel/remove it.
         * 3. Add a new order using the new price/quantity.
         */
        long orderIdToModify = cmd.getOrderId();
        if(orderBook.validateOrderExists(orderIdToModify)){
            orderBook.modifyOrder(orderIdToModify, cmd.getSide(), cmd.getNewPrice(), cmd.getNewQuantity(), cmd.getOrderId());
        }
    }
    public long marketLimitOrder(MarketOrderCommand cmd){
        /**
         * Given a market order:
         *
         * 1. Match immediately against the opposite side.
         * 2. Continue until:
         *    - quantity is filled, or
         *    - there is no more opposite liquidity.
         * 3. Do not rest the remaining quantity on the book.
         * 4. If unfilled quantity remains, reject/cancel it.
         */
        long remainingQuantity = cmd.getQuantity();
        if(cmd.validateCommand()){
            if(cmd.getSide().equals(Side.BUY)){
                remainingQuantity = orderBook.matchBuyOrderOnAsksMarket(cmd.getQuantity());
            }else{
                remainingQuantity = orderBook.matchSellOrderOnBidsMarket(cmd.getQuantity());
            }
        }
        return remainingQuantity;

    }
}
