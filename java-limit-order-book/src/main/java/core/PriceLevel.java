package core;

import event.TradeDTO;

import java.util.*;

public class PriceLevel {
    private LinkedHashMap<Long,Order> orders;
    private long totalQuantity;
    private long orderCount;
    private Queue<TradeDTO> tradeEvents;

    public PriceLevel(LinkedHashMap<Long,Order> orders, long totalQuantity, long orderCount){
        this.orders = orders;
        this.totalQuantity = totalQuantity;
        this.orderCount = orderCount;
        this.tradeEvents = new ArrayDeque<>();
    }

    public LinkedHashMap<Long,Order> getOrders() {
        return orders;
    }

    public long getTotalQuantity() {
        return totalQuantity;
    }

    public long getOrderCount() {
        return orderCount;
    }

    public Queue<TradeDTO> fulfilOrder(long requestOrderId, long requestQuantity, Map<Long, Order> ordersById){
        tradeEvents.clear(); //Reset Trade Events queue
        long remainingRequestQty = requestQuantity;
//        BUY fully
        Iterator<Map.Entry<Long, Order>> iterator = orders.entrySet().iterator();
        while(iterator.hasNext() && remainingRequestQty>0 && totalQuantity>0){
            Map.Entry<Long, Order> entry = iterator.next();
            Order order = entry.getValue();
            long orderQty = order.getRemainingQuantity();
            if(orderQty>remainingRequestQty){
                order.setRemainingQuantity(orderQty-remainingRequestQty);
                totalQuantity-=remainingRequestQty;
                TradeDTO tradeEvent = createTradeEvent(requestOrderId,order.getSide(),order.getOrderId(),order.getPrice(),remainingRequestQty);
                tradeEvents.add(tradeEvent);
                remainingRequestQty = 0;
            }else{
                iterator.remove();
                ordersById.remove(order.getOrderId());
                orderCount--;
                remainingRequestQty-=orderQty;
                totalQuantity-=orderQty;
                TradeDTO tradeEvent = createTradeEvent(requestOrderId,order.getSide(),order.getOrderId(),order.getPrice(),order.getRemainingQuantity());
                tradeEvents.add(tradeEvent);
            }
        }
        return tradeEvents;
    }

    public void addOrder(Order order){
        orders.put(order.getOrderId(),order);
        totalQuantity+=order.getQuantity();
        orderCount+=1;

    }

    public void removeOrder(Order order){
        orders.remove(order.getOrderId());
        orderCount--;
        totalQuantity-=order.getRemainingQuantity();
    }

    public TradeDTO createTradeEvent(long requestOrderId, Side priceLevelSide,long priceLevelOrderId, long price, long quantity){
        if(priceLevelSide.equals(Side.BUY)){
            return new TradeDTO(priceLevelOrderId,requestOrderId,price, quantity);
        }else{
            return new TradeDTO(requestOrderId,priceLevelOrderId,price,quantity);
        }
    }
}
