package core;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayDeque;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class PriceLevelTest {
    ArrayDeque<Order> orderDeque;
    private Map<Long, Order> ordersById;
    @Before
    public void setup(){
        orderDeque = new ArrayDeque<>();
        ordersById = new LinkedHashMap<>();
    }

    @Test
    public void buyFullyTest(){
        Order order1 = new Order(1L,1L,Side.SELL,1_00,10, new Date().getTime());
        Order order2 = new Order(2L,1L,Side.SELL, 1_00,5,new Date().getTime());
        orderDeque.add(order1);
        orderDeque.add(order2);
        ordersById.put(1L, order1);
        ordersById.put(2L, order1);

        PriceLevel priceLevel = new PriceLevel(orderDeque, order1.getQuantity()+order2.getQuantity(),orderDeque.size());

        long requestQtyRemaining = priceLevel.fulfilOrder(15, ordersById);

        Assert.assertEquals(requestQtyRemaining,0);
        Assert.assertEquals(orderDeque.size(),0);
    }

    @Test
    public void buyFullyWithRemainingTest(){
        Order order1 = new Order(1L,1L,Side.SELL,1_00,10, new Date().getTime());
        Order order2 = new Order(2L,1L,Side.SELL, 1_00,8,new Date().getTime());
        orderDeque.add(order1);
        orderDeque.add(order2);
        ordersById.put(1L, order1);
        ordersById.put(2L, order1);
        PriceLevel priceLevel = new PriceLevel(orderDeque, order1.getQuantity()+order2.getQuantity(), orderDeque.size());

        long requestQtyRemaining = priceLevel.fulfilOrder(15, ordersById);

        Assert.assertEquals(requestQtyRemaining,0);
        Assert.assertEquals(orderDeque.size(),1);
        Assert.assertEquals(orderDeque.getFirst().getRemainingQuantity(),3);

    }

    @Test
    public void buyPartiallyTest(){
        Order order1 = new Order(1L,1L,Side.SELL,1_00,10, new Date().getTime());
        Order order2 = new Order(2L,1L,Side.SELL, 1_00,5,new Date().getTime());
        orderDeque.add(order1);
        orderDeque.add(order2);
        ordersById.put(1L, order1);
        ordersById.put(2L, order1);
        PriceLevel priceLevel = new PriceLevel(orderDeque, order1.getQuantity()+ order2.getQuantity(), orderDeque.size());

        long requestQtyRemaining = priceLevel.fulfilOrder(20, ordersById);

        Assert.assertEquals(requestQtyRemaining,5);
        Assert.assertEquals(orderDeque.size(),0);
    }

    @Test
    public void buyNotFulfilledAtAll(){
        PriceLevel priceLevel = new PriceLevel(orderDeque, 0, orderDeque.size());

        long requestQtyRemaining = priceLevel.fulfilOrder(20, ordersById);

        Assert.assertEquals(requestQtyRemaining,20);
        Assert.assertEquals(orderDeque.size(),0);
    }
}
