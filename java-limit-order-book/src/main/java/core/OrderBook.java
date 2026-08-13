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
        if(asks.size()<=0) {
            restUnfilledOrderQuantities(buyPrice, buyQuantity, Side.BUY);
            return buyQuantity;
        }
        long remainingRequestQty = buyQuantity;
        long lowestAsk = asks.firstKey();
        if(lowestAsk>buyPrice){
            // Rest full order since no match
            restUnfilledOrderQuantities(buyPrice, buyQuantity, Side.BUY);
        }else {
            //Cheapest SELLs get executed first
            while (remainingRequestQty > 0 && asks.size() > 0 && asks.firstKey() <= buyPrice) {
                lowestAsk = asks.firstKey();
                PriceLevel lowestSellingPriceLevel = asks.get(lowestAsk);
                long orderIdToMatch = lowestSellingPriceLevel.getOrders().peekFirst().getOrderId();
                remainingRequestQty = lowestSellingPriceLevel.fulfilOrder(remainingRequestQty);
                if(remainingRequestQty>0){
                    //order fully filled, remove order from ordersById
                    System.out.println("removing order");
                    ordersById.remove(orderIdToMatch);
                }
                checkAndCleanupPriceLevel(lowestAsk, Side.SELL);
            }
            // if there are remaining qty unfilled, move to resting order
            if (remainingRequestQty > 0) {
                restUnfilledOrderQuantities(buyPrice, remainingRequestQty, Side.BUY);
            }
        }
        return remainingRequestQty;
    }


    public long matchSellOrderOnBids(long sellPrice, long sellQuantity){
        if(bids.size()<=0) {
            restUnfilledOrderQuantities(sellPrice, sellQuantity, Side.SELL);
            return sellQuantity;
        }
        long remainingRequestQty = sellQuantity;
        long highestBid = bids.lastKey();
        if(highestBid<sellPrice){
            // Rest full order since no match
            restUnfilledOrderQuantities(sellPrice, sellQuantity, Side.SELL);
        }else {
            //Highest BUYs get executed first
            while (remainingRequestQty > 0 && bids.size()>0 && bids.lastKey() >= sellPrice) {
                highestBid = bids.lastKey();
                PriceLevel highestBuyPriceLevel = bids.get(highestBid);
                long orderIdToMatch = highestBuyPriceLevel.getOrders().peekFirst().getOrderId();
                remainingRequestQty = highestBuyPriceLevel.fulfilOrder(remainingRequestQty);
                if(remainingRequestQty>0){
                    //order fully filled, remove order from ordersById
                    System.out.println("removing order");
                    ordersById.remove(orderIdToMatch);
                }
                checkAndCleanupPriceLevel(highestBid, Side.BUY);
                // if there are remaining qty unfilled, move to resting order
            }
            if (remainingRequestQty > 0) {
                restUnfilledOrderQuantities(sellPrice,remainingRequestQty,Side.SELL);
            }
        }
        return remainingRequestQty;
    }

    public void restUnfilledOrderQuantities(long price, long quantity, Side side){
        if(side.equals(Side.BUY)){
            if (bids.get(price) == null) {
                PriceLevel newBuyPriceLevel = createNewPriceLevel(price, quantity, Side.BUY);
                bids.put(price, newBuyPriceLevel);
            } else {
                long orderId = ordersById.size() + 1;
                Order newBuyOrder = new Order(orderId, symbolId, Side.BUY, price, quantity, new Date().getTime());
                bids.get(price).addOrder(newBuyOrder);
                ordersById.put(orderId, newBuyOrder);
            }
        }else{
            if (asks.get(price) == null) {
                PriceLevel newSellPriceLevel = createNewPriceLevel(price, quantity, Side.SELL);
                asks.put(price, newSellPriceLevel);
            } else {
                long orderId = ordersById.size() + 1;
                Order newSellOrder = new Order(orderId, symbolId, Side.SELL, price, quantity, new Date().getTime());
                asks.get(price).addOrder(newSellOrder);
                ordersById.put(orderId, newSellOrder);
            }
        }
    }

    public long matchBuyOrderOnAsksMarket(long buyQuantity){
        long remainingRequestQty = buyQuantity;
        //Cheapest SELLs get executed first
        while (remainingRequestQty > 0 && asks.size() > 0) {
            long lowestAsk = asks.firstKey();
            PriceLevel lowestSellingPriceLevel = asks.get(lowestAsk);
            long orderIdToMatch = lowestSellingPriceLevel.getOrders().peekFirst().getOrderId();
            remainingRequestQty = lowestSellingPriceLevel.fulfilOrder(remainingRequestQty);
            if(remainingRequestQty>0){
                //ask fully filled, remove order from ordersById
                System.out.println("removing order");
                ordersById.remove(orderIdToMatch);
            }
            checkAndCleanupPriceLevel(lowestAsk, Side.SELL);
        }
        // if there are remaining qty unfilled, disregard them
        return remainingRequestQty;
    }

    public long matchSellOrderOnBidsMarket(long sellQuantity){
        long remainingRequestQty = sellQuantity;
        //Highest BUYs get executed first
        while (remainingRequestQty > 0 && bids.size()>0) {
            long highestBid = bids.lastKey();
            PriceLevel highestBuyPriceLevel = bids.get(highestBid);
            long orderIdToMatch = highestBuyPriceLevel.getOrders().peekFirst().getOrderId();
            remainingRequestQty = highestBuyPriceLevel.fulfilOrder(remainingRequestQty);
            if(remainingRequestQty>0){
                //bid fully filled, remove order from ordersById
                ordersById.remove(orderIdToMatch);
            }
            checkAndCleanupPriceLevel(highestBid, Side.BUY);
        }
        // if there are remaining qty unfilled, move to resting order
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
