package core;

import java.util.*;

public class OrderBook {
    private TreeMap<Long, PriceLevel> bids;
    private TreeMap<Long, PriceLevel> asks;
    private Map<Long, Order> ordersById;

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

    public boolean validateOrderExists(long orderId){
        return ordersById.containsKey(orderId);
    }

    public long matchBuyOrderOnAsks(long buyPrice, long buyQuantity){
        long remainingRequestQty = buyQuantity;
        long lowestAsk = asks.firstKey();
        if(lowestAsk>buyPrice){
//            TODO: create buy order for buyPrice since no sells available
            long newBuyOrderId = ordersById.size()+1;
            long symbolId = 1L;
            Order buyOrder = new Order(newBuyOrderId, symbolId,Side.BUY,buyPrice,buyQuantity,new Date().getTime());
            if(bids.get(buyPrice)==null){
                ArrayDeque<Order> newBuyDeque = new ArrayDeque<>();
                newBuyDeque.add(buyOrder);
                PriceLevel newBuyPriceLevel = new PriceLevel(newBuyDeque,buyQuantity,1);
                bids.put(buyPrice, newBuyPriceLevel);
            }else{
                PriceLevel priceLevel = bids.get(buyPrice);
                priceLevel.addOrder(buyOrder);
            }
        }else{
        /**
         * Cheapest SELLs get sold first
         */
            PriceLevel lowestPriceLevel = asks.get(lowestAsk);
            remainingRequestQty = lowestPriceLevel.fulfilOrder(buyQuantity);
        }

        return remainingRequestQty;
    }
}
