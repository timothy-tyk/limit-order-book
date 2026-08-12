package core;

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
            priceLevel = bids.get(order.getPrice());
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

    public long matchBuyOrderOnAsks(long buyPrice, long buyQuantity){
        long remainingRequestQty = buyQuantity;
        long lowestAsk = asks.firstKey();
        if(lowestAsk>buyPrice){
            if(bids.get(buyPrice)==null){
                PriceLevel newBuyPriceLevel = createNewPriceLevel(buyPrice,buyQuantity,Side.BUY);
                bids.put(buyPrice, newBuyPriceLevel);
            }else{
                long newBuyOrderId = ordersById.size()+1;
                Order buyOrder = new Order(newBuyOrderId, symbolId,Side.BUY,buyPrice,buyQuantity,new Date().getTime());
                PriceLevel priceLevel = bids.get(buyPrice);
                priceLevel.addOrder(buyOrder);
                ordersById.put(newBuyOrderId, buyOrder);
            }
        }else {
            //Cheapest SELLs get executed first
            while (remainingRequestQty > 0 && asks.size() > 0 && asks.firstKey() <= buyPrice) {
                lowestAsk = asks.firstKey();
                PriceLevel lowestSellingPriceLevel = asks.get(lowestAsk);
                remainingRequestQty = lowestSellingPriceLevel.fulfilOrder(remainingRequestQty);
                checkAndCleanupPriceLevel(lowestAsk, Side.SELL);
            }

        // if there are remaining qty unfilled, move to resting order
        if (remainingRequestQty > 0) {
            if (bids.get(buyPrice) == null) {
                PriceLevel newBuyPriceLevel = createNewPriceLevel(buyPrice, remainingRequestQty, Side.BUY);
                bids.put(buyPrice, newBuyPriceLevel);
            } else {
                long orderId = ordersById.size() + 1;
                Order newBuyOrder = new Order(orderId, symbolId, Side.BUY, buyPrice, remainingRequestQty, new Date().getTime());
                bids.get(buyPrice).addOrder(newBuyOrder);
                ordersById.put(orderId, newBuyOrder);
            }
        }
        }
        return remainingRequestQty;
    }

    public long matchSellOrderOnBids(long sellPrice, long sellQuantity){
        long remainingRequestQty = sellQuantity;
        long highestBid = bids.lastKey();
        if(highestBid<sellPrice){
            if(asks.get(sellPrice)==null) {
                PriceLevel newSellPriceLevel = createNewPriceLevel(sellPrice,sellQuantity,Side.SELL);
                asks.put(sellPrice, newSellPriceLevel);
            }else{
                long orderId = ordersById.size()+1;
                Order newSellOrder = new Order(orderId,symbolId,Side.SELL,sellPrice,sellQuantity,new Date().getTime());
                PriceLevel priceLevel = asks.get(sellPrice);
                priceLevel.addOrder(newSellOrder);
                ordersById.put(orderId, newSellOrder);
            }
        }else {
            //Highest BUYs get executed first
            while (remainingRequestQty > 0 && bids.size()>0 && bids.lastKey() >= sellPrice) {
                highestBid = bids.lastKey();
                PriceLevel highestBuyPriceLevel = bids.get(highestBid);
                remainingRequestQty = highestBuyPriceLevel.fulfilOrder(remainingRequestQty);
                checkAndCleanupPriceLevel(highestBid, Side.BUY);
                // if there are remaining qty unfilled, move to resting order
            }
            if (remainingRequestQty > 0) {
                if (asks.get(sellPrice) == null) {
                    PriceLevel newSellPriceLevel = createNewPriceLevel(sellPrice, remainingRequestQty, Side.SELL);
                    asks.put(sellPrice, newSellPriceLevel);
                } else {
                    long orderId = ordersById.size() + 1;
                    Order newSellOrder = new Order(orderId, symbolId, Side.SELL, sellPrice, remainingRequestQty, new Date().getTime());
                    asks.get(sellPrice).addOrder(newSellOrder);
                    ordersById.put(orderId, newSellOrder);
                }
            }
        }
        return remainingRequestQty;
    }

    public PriceLevel createNewPriceLevel(long price, long quantity, Side side){
        ArrayDeque<Order> orders = new ArrayDeque<>();
        long orderId = ordersById.size()+1;
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

    public void modifyOrder(long orderId, Side newSide, long newPrice, long newQuantity){
        if(validateOrderExists(orderId)){
            cancelOrder(orderId);
            long newOrderId = ordersById.size()+1;
            Order newOrderToAdd = new Order(newOrderId,symbolId,newSide,newPrice,newQuantity,new Date().getTime());
            PriceLevel priceLevel;
            if(newSide.equals(Side.BUY)){
               if(bids.containsKey(newPrice)){
                   addOrder(newOrderToAdd);
               }else{
                   bids.put(newPrice, createNewPriceLevel(newPrice, newQuantity, newSide));
               }
            }else{
                if(asks.containsKey(newPrice)){
                    addOrder(newOrderToAdd);
                }else{
                    asks.put(newPrice, createNewPriceLevel(newPrice, newQuantity, newSide));
                }
            }
        }
    }

}
