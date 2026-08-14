package core;

import event.EventListener;
import event.MarketOrderAccepted;
import event.Trade;
import event.TradeDTO;

import java.util.*;

public class OrderBook {
    private TreeMap<Long, PriceLevel> bids;
    private TreeMap<Long, PriceLevel> asks;
    private Map<Long, Order> ordersById;

    private final long symbolId = 1L;

    public OrderBook(){
        this.bids = new TreeMap<>();
        this.asks = new TreeMap<>();
        this.ordersById = new LinkedHashMap<>();
    }

    public TreeMap<Long, PriceLevel> getBids() {
        return bids;
    }

    public TreeMap<Long, PriceLevel> getAsks() {
        return asks;
    }

    public void setBids(TreeMap<Long, PriceLevel> bids){
        this.bids = bids;
    }

    public void setAsks(TreeMap<Long, PriceLevel> asks){
        this.asks = asks;
    }

    public Map<Long, Order> getOrdersById() {
        return ordersById;
    }

    public void addOrder(Order order){
        PriceLevel priceLevel = null;
        if(order.getSide().equals(Side.BUY)){
            priceLevel = bids.get(order.getPrice())!=null? bids.get(order.getPrice()):createNewPriceLevel(order.getPrice(), order.getQuantity(), order.getSide(),order.getOrderId());
            priceLevel.addOrder(order);
            ordersById.put(order.getOrderId(), order);
        }else{
            priceLevel = asks.get(order.getPrice());
            priceLevel.addOrder(order);
            ordersById.put(order.getOrderId(), order);
        }
    }

    public boolean validateOrderExists(long orderId){
        return ordersById.containsKey(orderId);
    }

    public Queue<TradeDTO> matchBuyOrderOnAsks(long buyPrice, long buyQuantity, long orderId){
        Queue<TradeDTO> tradeDTOs = new ArrayDeque<>();
        if(asks.size()<=0) {
            restUnfilledOrderQuantities(buyPrice, buyQuantity, Side.BUY, orderId);
            return tradeDTOs;
        }
        long remainingRequestQty = buyQuantity;
        long lowestAsk = asks.firstKey();
        if(lowestAsk>buyPrice){
            // Rest full order since no match
            restUnfilledOrderQuantities(buyPrice, buyQuantity, Side.BUY, orderId);
        }else {
            //Cheapest SELLs get executed first
            while (!asks.isEmpty() && asks.firstKey() <= buyPrice) {
                lowestAsk = asks.firstKey();
                PriceLevel lowestSellingPriceLevel = asks.get(lowestAsk);
                tradeDTOs.addAll(lowestSellingPriceLevel.fulfilOrder(orderId, remainingRequestQty, ordersById));
                checkAndCleanupPriceLevel(lowestAsk, Side.SELL);
            }
            // if there are remaining qty unfilled, move to resting order
            if (remainingRequestQty > 0) {
                restUnfilledOrderQuantities(buyPrice, remainingRequestQty, Side.BUY, orderId);
            }
        }
        return tradeDTOs;
    }


    public Queue<TradeDTO> matchSellOrderOnBids(long sellPrice, long sellQuantity, long orderId){
        Queue<TradeDTO> tradeDTOs = new ArrayDeque<>();
        if(bids.size()<=0) {
            restUnfilledOrderQuantities(sellPrice, sellQuantity, Side.SELL, orderId);
            return tradeDTOs;
        }
        long remainingRequestQty = sellQuantity;
        long highestBid = bids.lastKey();
        if(highestBid<sellPrice){
            // Rest full order since no match
            restUnfilledOrderQuantities(sellPrice, sellQuantity, Side.SELL, orderId);
        }else {
            //Highest BUYs get executed first
            while (!bids.isEmpty() && bids.lastKey() >= sellPrice) {
                highestBid = bids.lastKey();
                PriceLevel highestBuyPriceLevel = bids.get(highestBid);
                tradeDTOs.addAll(highestBuyPriceLevel.fulfilOrder(orderId, remainingRequestQty, ordersById));
                checkAndCleanupPriceLevel(highestBid, Side.BUY);
            }
            // if there are remaining qty unfilled, move to resting order
            if (remainingRequestQty > 0) {
                restUnfilledOrderQuantities(sellPrice,remainingRequestQty,Side.SELL, orderId);
            }
        }
        return tradeDTOs;
    }

    public void restUnfilledOrderQuantities(long price, long quantity, Side side, long orderId){
        if(side.equals(Side.BUY)){
            if (bids.get(price) == null) {
                PriceLevel newBuyPriceLevel = createNewPriceLevel(price, quantity, Side.BUY, orderId);
                bids.put(price, newBuyPriceLevel);
            } else {
                Order newBuyOrder = new Order(orderId, symbolId, Side.BUY, price, quantity, new Date().getTime());
                bids.get(price).addOrder(newBuyOrder);
                ordersById.put(orderId, newBuyOrder);
            }
        }else{
            if (asks.get(price) == null) {
                PriceLevel newSellPriceLevel = createNewPriceLevel(price, quantity, Side.SELL, orderId);
                asks.put(price, newSellPriceLevel);
            } else {
                Order newSellOrder = new Order(orderId, symbolId, Side.SELL, price, quantity, new Date().getTime());
                asks.get(price).addOrder(newSellOrder);
                ordersById.put(orderId, newSellOrder);
            }
        }
    }

    public Queue<TradeDTO> matchBuyOrderOnAsksMarket(long buyQuantity, long orderId){
        Queue<TradeDTO> tradeDTOs = new ArrayDeque<>();
        long remainingRequestQty = buyQuantity;
        //Cheapest SELLs get executed first
        while (!asks.isEmpty()) {
            long lowestAsk = asks.firstKey();
            PriceLevel lowestSellingPriceLevel = asks.get(lowestAsk);
            tradeDTOs.addAll(lowestSellingPriceLevel.fulfilOrder(orderId,remainingRequestQty, ordersById));
            checkAndCleanupPriceLevel(lowestAsk, Side.SELL);
        }
        // if there are remaining qty unfilled, disregard them
        return tradeDTOs;
    }

    public Queue<TradeDTO> matchSellOrderOnBidsMarket(long sellQuantity, long orderId){
        Queue<TradeDTO> tradeDTOs = new ArrayDeque<>();
        long remainingRequestQty = sellQuantity;
        //Highest BUYs get executed first
        while (!bids.isEmpty()) {
            long highestBid = bids.lastKey();
            PriceLevel highestBuyPriceLevel = bids.get(highestBid);
            tradeDTOs.addAll(highestBuyPriceLevel.fulfilOrder(orderId, remainingRequestQty, ordersById));
            checkAndCleanupPriceLevel(highestBid, Side.BUY);
        }
        // if there are remaining qty unfilled, disregard them
        return tradeDTOs;
    }

    public PriceLevel createNewPriceLevel(long price, long quantity, Side side, long orderId){
        ArrayDeque<Order> orders = new ArrayDeque<>();
        Order newOrder = new Order(orderId, symbolId,side, price, quantity, new Date().getTime());
        orders.add(newOrder);
        ordersById.put(orderId, newOrder);
        return new PriceLevel(orders,quantity,1);
    }


    public void checkAndCleanupPriceLevel(long price, Side side) {
//      Remove price level when qty = 0
        PriceLevel priceLevel;
        if (side.equals(Side.BUY)) {
            priceLevel = bids.get(price);
            if (priceLevel.getOrderCount() <= 0 && priceLevel.getTotalQuantity() <= 0 && priceLevel.getOrders().isEmpty()) {
                bids.remove(price);
            }
        } else {
            priceLevel = asks.get(price);
            if (priceLevel.getOrderCount() <= 0 && priceLevel.getTotalQuantity() <= 0 && priceLevel.getOrders().isEmpty()) {
                asks.remove(price);
            }
        }
    }

    public void cancelOrder(long orderId){
        if(validateOrderExists(orderId)) {
            Order orderToCancel = ordersById.get(orderId);
            long orderPrice = orderToCancel.getPrice();
            Side orderSide = orderToCancel.getSide();
            if(orderSide.equals(Side.BUY)){
                PriceLevel buyPriceLevel = bids.get(orderPrice);
                buyPriceLevel.removeOrder(orderToCancel);
                checkAndCleanupPriceLevel(orderPrice, orderSide);
            }else{
                PriceLevel sellPriceLevel = asks.get(orderPrice);
                sellPriceLevel.removeOrder(orderToCancel);
                checkAndCleanupPriceLevel(orderPrice, orderSide);
            }
            ordersById.remove(orderId);
        }
    }

    public Queue<TradeDTO> modifyOrder(long orderId, Side newSide, long newPrice, long newQuantity, long newOrderId){
        Queue<TradeDTO> tradeDTOs = new ArrayDeque<>();
        if(validateOrderExists(orderId)){
            cancelOrder(orderId);
            if(newSide.equals(Side.BUY)){
                tradeDTOs = matchBuyOrderOnAsks(newPrice,newQuantity,newOrderId);
            }else{
                tradeDTOs = matchSellOrderOnBids(newPrice,newQuantity, newOrderId);
            }
        }
        return tradeDTOs;
    }



}
