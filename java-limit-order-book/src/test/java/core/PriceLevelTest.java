package core;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayDeque;
import java.util.Date;

public class PriceLevelTest {
    @Before
    public void setup(){

    }

    @Test
    public void buyFullyTest(){
        Order order1 = new Order(1L,1L,Side.SELL,1_00,10, new Date().getTime());
        Order order2 = new Order(2L,1L,Side.SELL, 1_00,5,new Date().getTime());
        ArrayDeque<Order> sellDeque = new ArrayDeque<>();
        sellDeque.add(order1);
        sellDeque.add(order2);
        PriceLevel priceLevel = new PriceLevel(sellDeque, order1.getQuantity()+order2.getQuantity(),sellDeque.size());

        long requestQtyRemaining = priceLevel.fulfilOrder(15);

        Assert.assertEquals(requestQtyRemaining,0);
        Assert.assertEquals(sellDeque.size(),0);
    }

    @Test
    public void buyFullyWithRemainingTest(){
        Order order1 = new Order(1L,1L,Side.SELL,1_00,10, new Date().getTime());
        Order order2 = new Order(2L,1L,Side.SELL, 1_00,8,new Date().getTime());
        ArrayDeque<Order> sellDeque = new ArrayDeque<>();
        sellDeque.add(order1);
        sellDeque.add(order2);
        PriceLevel priceLevel = new PriceLevel(sellDeque, order1.getQuantity()+order2.getQuantity(), sellDeque.size());

        long requestQtyRemaining = priceLevel.fulfilOrder(15);

        Assert.assertEquals(requestQtyRemaining,0);
        Assert.assertEquals(sellDeque.size(),1);
        Assert.assertEquals(sellDeque.getFirst().getRemainingQuantity(),3);

    }

    @Test
    public void buyPartiallyTest(){
        Order order1 = new Order(1L,1L,Side.SELL,1_00,10, new Date().getTime());
        Order order2 = new Order(2L,1L,Side.SELL, 1_00,5,new Date().getTime());
        ArrayDeque<Order> sellDeque = new ArrayDeque<>();
        sellDeque.add(order1);
        sellDeque.add(order2);
        PriceLevel priceLevel = new PriceLevel(sellDeque, order1.getQuantity()+ order2.getQuantity(), sellDeque.size());

        long requestQtyRemaining = priceLevel.fulfilOrder(20);

        Assert.assertEquals(requestQtyRemaining,5);
        Assert.assertEquals(sellDeque.size(),0);
    }

    @Test
    public void buyNotFulfilledAtAll(){
        ArrayDeque<Order> sellDeque = new ArrayDeque<>();
        PriceLevel priceLevel = new PriceLevel(sellDeque, 0, sellDeque.size());

        long requestQtyRemaining = priceLevel.fulfilOrder(20);

        Assert.assertEquals(requestQtyRemaining,20);
        Assert.assertEquals(sellDeque.size(),0);
    }
}
