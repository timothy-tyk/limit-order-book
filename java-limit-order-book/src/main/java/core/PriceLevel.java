package core;

import event.TradeDTO;

import java.util.*;

public class PriceLevel {
    private ArrayDeque<Order> orders;
    private long totalQuantity;
    private long orderCount;

    public PriceLevel(ArrayDeque<Order> orders, long totalQuantity, long orderCount){
        this.orders = orders;
        this.totalQuantity = totalQuantity;
        this.orderCount = orderCount;
    }

    public ArrayDeque<Order> getOrders() {
        return orders;
    }

    public long getTotalQuantity() {
        return totalQuantity;
    }

    public long getOrderCount() {
        return orderCount;
    }

    public Queue<TradeDTO> fulfilOrder(long requestOrderId, long requestQuantity, Map<Long, Order> ordersById){
        Queue<TradeDTO> tradeEvents = new ArrayDeque<>();
        long remainingRequestQty = requestQuantity;
//        BUY fully
        while(orders.peek()!=null && remainingRequestQty>0 && totalQuantity>0){
            Order order = orders.getFirst();
            long orderQty = order.getRemainingQuantity();
            if(orderQty>remainingRequestQty){
                order.setRemainingQuantity(orderQty-remainingRequestQty);
                totalQuantity-=remainingRequestQty;
                TradeDTO tradeEvent = createTradeEvent(requestOrderId,order.getSide(),order.getOrderId(),order.getPrice(),remainingRequestQty);
                tradeEvents.add(tradeEvent);
                remainingRequestQty = 0;
            }else{
                long orderIdToRemove = orders.getFirst().getOrderId();
                orders.removeFirst();
                ordersById.remove(orderIdToRemove);
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
        orders.add(order);
        totalQuantity+=order.getQuantity();
        orderCount+=1;

    }

    public void removeOrder(Order order){
        orders.remove(order);
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
