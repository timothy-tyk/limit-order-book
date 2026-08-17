package engine;

import benchmark.LatencyRecorder;
import benchmark.WorkloadProfile;
import command.*;
import core.OrderBook;
import core.Side;
import event.*;
import validation.OrderRejectedReason;

import java.util.ArrayDeque;
import java.util.Queue;

public class SingleThreadedMatchingEngine implements MatchingEngine {
    private long lastProcessedSequence = 0;
    private OrderBook orderBook;
    private final EventListener eventListener;
    private long nextEventSequence = 1;
    private LatencyRecorder latencyRecorder;


    public SingleThreadedMatchingEngine(EventListener eventListener, LatencyRecorder recorder) {
        this.orderBook = new OrderBook();
        this.eventListener = eventListener;
        this. latencyRecorder = recorder;
    }

    @Override
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
        long latencyStart = System.nanoTime();
        switch(command){
            case AddLimitOrderCommand cmd -> addLimitOrder(cmd);
            case CancelOrderCommand cmd -> cancelLimitOrder(cmd);
            case ModifyOrderCommand cmd -> modifyLimitOrder(cmd);
            case MarketOrderCommand cmd -> marketLimitOrder(cmd);
            default -> throw new IllegalStateException("Unexpected value: " + command);
        }
        long latencyEnd = System.nanoTime();
        latencyRecorder.record(latencyEnd-latencyStart);

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
//        long addStart = System.nanoTime();
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
            if(cmd.getPrice()<=0) emitOrderRejected(cmd.sequence(),cmd.getOrderId(), OrderRejectedReason.INVALID_PRICE);
            else if(cmd.getQuantity()<=0) emitOrderRejected(cmd.sequence(),cmd.getOrderId(), OrderRejectedReason.INVALID_QTY);
            else if(!orderBook.validateOrderExists(cmd.getOrderId())) emitOrderRejected(cmd.sequence(),cmd.getOrderId(), OrderRejectedReason.UNKNOWN_ORDER);
        }
//        long addEnd = System.nanoTime();
//        System.out.println("Add: "+ (addEnd-addStart));
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
//        long cancelStart = System.nanoTime();
        long orderIdToCancel = cmd.getOrderId();
        if(orderBook.validateOrderExists(orderIdToCancel)){
            emitOrderCancelled(cmd.sequence(),orderIdToCancel);
            orderBook.cancelOrder(orderIdToCancel);
        }else{
            if(!orderBook.validateOrderExists(cmd.getOrderId())) emitOrderRejected(cmd.sequence(),cmd.getOrderId(), OrderRejectedReason.UNKNOWN_ORDER);
        }
//        long cancelEnd = System.nanoTime();
//        System.out.println("Cancel: "+ (cancelEnd-cancelStart));
    }
    void modifyLimitOrder(ModifyOrderCommand cmd){
        /**
         * 1. Find existing order by ID.
         * 2. Cancel/remove it.
         * 3. Add a new order using the new price/quantity.
         */
//        long modifyStart = System.nanoTime();
        long orderIdToModify = cmd.getOrderId();
        if(orderBook.validateOrderExists(orderIdToModify)){

            emitOrderModified(cmd.sequence(),orderIdToModify,cmd.getSide(),cmd.getNewPrice(),cmd.getNewQuantity());
            orderBook.modifyOrder(orderIdToModify, cmd.getSide(), cmd.getNewPrice(), cmd.getNewQuantity(), cmd.getOrderId());
        }else{
            if(cmd.getNewPrice()<=0) emitOrderRejected(cmd.sequence(),cmd.getOrderId(), OrderRejectedReason.INVALID_PRICE);
            else if(cmd.getNewQuantity()<=0) emitOrderRejected(cmd.sequence(),cmd.getOrderId(), OrderRejectedReason.INVALID_QTY);
            else if(!orderBook.validateOrderExists(cmd.getOrderId())) emitOrderRejected(cmd.sequence(),cmd.getOrderId(), OrderRejectedReason.UNKNOWN_ORDER);
        }
//        long modifyEnd = System.nanoTime();
//        System.out.println("Add: "+ (modifyEnd-modifyStart));
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
            if(cmd.getQuantity()<=0) emitMarketOrderRejected(cmd.sequence(),cmd.getOrderId(), OrderRejectedReason.INVALID_QTY);
            else if(!orderBook.validateOrderExists(cmd.getOrderId())) emitMarketOrderRejected(cmd.sequence(),cmd.getOrderId(), OrderRejectedReason.UNKNOWN_ORDER);
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

    void emitOrderRejected(long commandSequence, long orderId, OrderRejectedReason reason){
        OrderRejected orderRejected = new OrderRejected(nextEventSequence++,commandSequence, orderId, reason);
        eventListener.onEvent(orderRejected);
    }

    void emitMarketOrderAccepted(long commandSequence, long orderId, Side side , long quantity){
        MarketOrderAccepted marketOrderAccepted = new MarketOrderAccepted(nextEventSequence++,commandSequence,orderId,side,quantity);
        eventListener.onEvent(marketOrderAccepted);
    }
    void emitMarketOrderRejected(long commandSequence, long orderId, OrderRejectedReason reason){
        MarketOrderRejected marketOrderRejected = new MarketOrderRejected(nextEventSequence++,commandSequence,orderId, reason);
        eventListener.onEvent(marketOrderRejected);
    }

    void emitTradeEvent(long nextEventSequence, long commandSequence, long buyOrderId, long sellOrderId, long price, long quantity){
        Trade tradeEvent = new Trade(nextEventSequence,commandSequence,buyOrderId,sellOrderId,price,quantity);
        eventListener.onEvent(tradeEvent);
    }

    @Override
    public void showEventSummary(WorkloadProfile profile){
        System.out.println(profile.getName());
        System.out.println(eventListener.summary());
        System.out.println();
    }
}
