package core;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayDeque;
import java.util.Date;
import java.util.TreeMap;

public class OrderBookTest {
    OrderBook orderBook;

    @Before
    public void setup(){
        orderBook = new OrderBook();
        TreeMap<Long, PriceLevel> asks = new TreeMap<>();

        Order order1 = new Order(1L,1L,Side.SELL,1_00,10, new Date().getTime());
        Order order2 = new Order(2L,1L,Side.SELL, 1_00,5,new Date().getTime());
        ArrayDeque<Order> sellDeque1L = new ArrayDeque<>();
        sellDeque1L.add(order1);
        sellDeque1L.add(order2);
        PriceLevel priceLevel1L = new PriceLevel(sellDeque1L,order1.getQuantity()+order2.getQuantity(),sellDeque1L.size());

        Order order3 = new Order(3L,1L,Side.SELL,2_00,6, new Date().getTime());
        Order order4 = new Order(4L,1L,Side.SELL, 2_00,5,new Date().getTime());
        ArrayDeque<Order> sellDeque2L = new ArrayDeque<>();
        sellDeque2L.add(order1);
        sellDeque2L.add(order2);
        PriceLevel priceLevel2L = new PriceLevel(sellDeque2L,order3.getQuantity()+order4.getQuantity(),sellDeque2L.size());
        asks.put(1_00L, priceLevel1L);
        asks.put(2_00L, priceLevel2L);
        orderBook.setAsks(asks);
    }

    @Test
    public void orderBookMatchBuyOrderOnAsks(){
        long priceMatched = orderBook.matchBuyOrderOnAsks(1_50, 20);
        Assert.assertEquals(priceMatched, 5);
    }

    @Test
    public void orderBookUnableToMatchBuyOrderOnAsks(){
        long priceMatched = orderBook.matchBuyOrderOnAsks(50, 20);
        Assert.assertEquals(orderBook.getBids().size(),1);
        Assert.assertEquals(priceMatched, 20);
    }
}
