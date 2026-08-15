package engine;

import command.*;
import core.OrderBook;
import core.Side;
import event.*;

import java.util.ArrayDeque;
import java.util.Queue;

public class SingleThreadedMatchingEngine implements MatchingEngine {
    private long lastProcessedSequence = 0;
    private OrderBook orderBook;
    private final EventListener eventListener;
    private long nextEventSequence = 1;

    public SingleThreadedMatchingEngine(EventListener eventListener) {
        this.orderBook = new OrderBook();
        this.eventListener = eventListener;
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

    public void addLimitOrder(AddLimitOrderCommand cmd){
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
            emitOrderAccepted(cmd.sequence(),cmd.getOrderId(), cmd.getSide(),cmd.getPrice(),cmd.getQuantity());
            Queue<TradeDTO> tradeDTOs;
            if(cmd.getSide().equals(Side.BUY)){
                tradeDTOs = orderBook.matchBuyOrderOnAsks(cmd.getPrice(), cmd.getQuantity(), cmd.getOrderId());
            }else{
                tradeDTOs = orderBook.matchSellOrderOnBids(cmd.getPrice(), cmd.getQuantity(),cmd.getOrderId());
            }
            for(TradeDTO tradeDTO: tradeDTOs){
                emitTradeEvent(nextEventSequence++,cmd.sequence(), tradeDTO.getBuyOrderId(), tradeDTO.getSellOrderId(), tradeDTO.getPrice(), tradeDTO.getQuantity());
            }
        }else{
            emitOrderRejected(cmd.sequence(),cmd.getOrderId());
        }
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
            emitOrderCancelled(cmd.sequence(),orderIdToCancel);
            orderBook.cancelOrder(orderIdToCancel);
        }else{
            emitOrderRejected(cmd.sequence(),cmd.getOrderId());
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

            emitOrderModified(cmd.sequence(),orderIdToModify,cmd.getSide(),cmd.getNewPrice(),cmd.getNewQuantity());
            orderBook.modifyOrder(orderIdToModify, cmd.getSide(), cmd.getNewPrice(), cmd.getNewQuantity(), cmd.getOrderId());
        }else{
            emitOrderRejected(cmd.sequence(),cmd.getOrderId());
        }
    }
    public void marketLimitOrder(MarketOrderCommand cmd){
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
        if(cmd.validateCommand()){
            emitMarketOrderAccepted(cmd.sequence(), cmd.getOrderId(), cmd.getSide(),cmd.getQuantity());
            Queue<TradeDTO> tradeDTOs;
            if(cmd.getSide().equals(Side.BUY)){
                tradeDTOs = orderBook.matchBuyOrderOnAsksMarket(cmd.getQuantity(), cmd.getOrderId());
            }else{
                tradeDTOs = orderBook.matchSellOrderOnBidsMarket(cmd.getQuantity(), cmd.getOrderId());
            }
            for(TradeDTO tradeDTO:tradeDTOs){
                emitTradeEvent(nextEventSequence++,cmd.sequence(), tradeDTO.getBuyOrderId(), tradeDTO.getSellOrderId(), tradeDTO.getPrice(), tradeDTO.getQuantity());
            }
        }else{
            emitMarketOrderRejected(cmd.sequence(),cmd.getOrderId());
        }

    }

    void emitOrderAccepted(long commandSequence, long orderId, Side side ,long price, long quantity){
        OrderAccepted orderAccepted = new OrderAccepted(nextEventSequence++,commandSequence,orderId,side,price,quantity);
        eventListener.onEvent(orderAccepted);
    }

    void emitOrderModified(long commandSequence, long orderId, Side newSide ,long newPrice, long newQuantity){
        OrderModified orderModified = new OrderModified(nextEventSequence++,commandSequence,orderId,newSide,newPrice,newQuantity);
        eventListener.onEvent(orderModified);
    }

    void emitOrderCancelled(long commandSequence, long orderId){
        OrderCancelled orderCancelled = new OrderCancelled(nextEventSequence++,commandSequence, orderId);
        eventListener.onEvent(orderCancelled);
    }

    void emitOrderRejected(long commandSequence, long orderId){
        OrderRejected orderRejected = new OrderRejected(nextEventSequence++,commandSequence, orderId);
        eventListener.onEvent(orderRejected);
    }

    void emitMarketOrderAccepted(long commandSequence, long orderId, Side side , long quantity){
        MarketOrderAccepted marketOrderAccepted = new MarketOrderAccepted(nextEventSequence++,commandSequence,orderId,side,quantity);
        eventListener.onEvent(marketOrderAccepted);
    }
    void emitMarketOrderRejected(long commandSequence, long orderId){
        MarketOrderRejected marketOrderRejected = new MarketOrderRejected(nextEventSequence++,commandSequence,orderId);
        eventListener.onEvent(marketOrderRejected);
    }

    void emitTradeEvent(long nextEventSequence, long commandSequence, long buyOrderId, long sellOrderId, long price, long quantity){
        Trade tradeEvent = new Trade(nextEventSequence,commandSequence,buyOrderId,sellOrderId,price,quantity);
        eventListener.onEvent(tradeEvent);
    }
}
