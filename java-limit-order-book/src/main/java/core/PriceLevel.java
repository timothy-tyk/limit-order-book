package core;

import java.util.ArrayDeque;
import java.util.Map;

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

    public long fulfilOrder(long requestQuantity, Map<Long, Order> ordersById){
        long remainingRequestQty = requestQuantity;
//        BUY fully
        while(orders.peek()!=null && remainingRequestQty>0 && totalQuantity>0){
            Order order = orders.getFirst();
            long orderQty = order.getRemainingQuantity();
            if(orderQty>remainingRequestQty){
                order.setRemainingQuantity(orderQty-remainingRequestQty);
                totalQuantity-=remainingRequestQty;
                remainingRequestQty = 0;
            }else{
                long orderIdToRemove = orders.getFirst().getOrderId();
                orders.removeFirst();
                ordersById.remove(orderIdToRemove);
                orderCount--;
                remainingRequestQty-=orderQty;
                totalQuantity-=orderQty;
            }
        }
        return remainingRequestQty;
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
}
