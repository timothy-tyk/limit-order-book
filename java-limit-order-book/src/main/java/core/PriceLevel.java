package core;

import java.util.ArrayDeque;

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

    public void addOrder(Order order){
        orders.add(order);
    }

    public long fulfilOrder(long requestQuantity){
        long remainingRequestQty = requestQuantity;
//        BUY fully
        while(orders.peek()!=null && remainingRequestQty>0 && totalQuantity>0){
            Order order = orders.getFirst();
            long orderQty = order.getQuantity();
            if(orderQty>remainingRequestQty){
                order.setRemainingQuantity(orderQty-remainingRequestQty);
                totalQuantity-=remainingRequestQty;
                remainingRequestQty = 0;
            }else{
                orders.removeFirst();
                orderCount--;
                remainingRequestQty-=orderQty;
                totalQuantity-=orderQty;
            }
            System.out.println("Remaining request qty ="+remainingRequestQty);
        }
        return remainingRequestQty;
    }
}
