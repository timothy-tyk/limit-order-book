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
        PriceLevel priceLevelSell1L = new PriceLevel(sellDeque1L,order1.getQuantity()+order2.getQuantity(),sellDeque1L.size());

        Order order3 = new Order(3L,1L,Side.SELL,2_00,6, new Date().getTime());
        Order order4 = new Order(4L,1L,Side.SELL, 2_00,5,new Date().getTime());
        ArrayDeque<Order> sellDeque2L = new ArrayDeque<>();
        sellDeque2L.add(order3);
        sellDeque2L.add(order4);
        PriceLevel priceLevelSell2L = new PriceLevel(sellDeque2L,order3.getQuantity()+order4.getQuantity(),sellDeque2L.size());
        asks.put(1_00L, priceLevelSell1L);
        asks.put(2_00L, priceLevelSell2L);
        orderBook.setAsks(asks);

        TreeMap<Long, PriceLevel> bids = new TreeMap<>();

        Order order5 = new Order(5L,1L,Side.BUY,1_00,10,new Date().getTime());
        Order order6 = new Order(6L,1L,Side.BUY, 1_00,5,new Date().getTime());
        ArrayDeque<Order> buyDeque1L = new ArrayDeque<>();
        buyDeque1L.add(order5);
        buyDeque1L.add(order6);
        PriceLevel priceLevelBuy1L = new PriceLevel(buyDeque1L,order5.getQuantity()+order6.getQuantity(),buyDeque1L.size());

        Order order7 = new Order(7L,1L,Side.BUY,2_00,8,new Date().getTime());
        Order order8 = new Order(8L,1L,Side.BUY, 2_00,5,new Date().getTime());
        ArrayDeque<Order> buyDeque2L = new ArrayDeque<>();
        buyDeque2L.add(order7);
        buyDeque2L.add(order8);
        PriceLevel priceLevelBuy2L = new PriceLevel(buyDeque2L,order7.getQuantity()+order8.getQuantity(),buyDeque2L.size());
        bids.put(1_00L, priceLevelBuy1L);
        bids.put(2_00L, priceLevelBuy2L);
        orderBook.setBids(bids);
    }

    @Test
    public void orderBookMatchBuyOrderOnAsks(){
//      Buy as much as possible, remaining buy qty add to PriceLevel
        long remainingRequestQty = orderBook.matchBuyOrderOnAsks(1_50, 20);
        Assert.assertEquals(remainingRequestQty, 5);
        Assert.assertEquals(orderBook.getBids().containsKey(1_50L), true);
    }

    @Test
    public void orderBookMatchBuyOrderOnAsks2(){
//      Buy as much as possible, remaining buy qty add to PriceLevel
        long remainingRequestQty = orderBook.matchBuyOrderOnAsks(2_00, 20);
        Assert.assertEquals(remainingRequestQty, 0);
        Assert.assertEquals(orderBook.getBids().containsKey(1_50L), false);
    }

    @Test
    public void orderBookUnableToMatchBuyOrderOnAsks(){
//        Cant buy, buyPrice too low
        long remainingRequestQty = orderBook.matchBuyOrderOnAsks(50, 20);
        Assert.assertEquals(orderBook.getBids().containsKey(50L),true);
        Assert.assertEquals(remainingRequestQty, 20);
    }

    @Test
    public void orderBookMatchSellOrderOnBids(){
//        Sell as much as possible, remaining sell qty add to PriceLevel
        long remainingRequestQty = orderBook.matchSellOrderOnBids(1_50, 20);
        Assert.assertEquals(remainingRequestQty, 7);
        Assert.assertEquals(orderBook.getAsks().containsKey(1_50L), true);
    }

    @Test
    public void orderBookMatchSellOrderOnBids2(){
//        Sell as much as possible, remaining sell qty add to PriceLevel
        long remainingRequestQty = orderBook.matchSellOrderOnBids(1_00, 20);
        Assert.assertEquals(remainingRequestQty, 0);
        Assert.assertEquals(orderBook.getBids().containsKey(2_00L), false);
        Assert.assertEquals(orderBook.getBids().get(1_00L).getTotalQuantity(), 8);
    }

    @Test
    public void orderBookUnableToMatchSellOrderOnBids(){
        long remainingRequestQty = orderBook.matchSellOrderOnBids(500, 20);
        Assert.assertEquals(orderBook.getAsks().containsKey(500L),true);
        Assert.assertEquals(remainingRequestQty, 20);
    }

    @Test
    public void orderBookMatchBuyOrderOnAsksMarket(){
//      Buy as much as possible, disregard remaining unfilled qty
        long remainingRequestQty = orderBook.matchBuyOrderOnAsksMarket(20);
        Assert.assertEquals(remainingRequestQty, 0);
        Assert.assertEquals(orderBook.getAsks().containsKey(1_00L), false);
        Assert.assertEquals(orderBook.getAsks().get(2_00L).getTotalQuantity(), 6);
    }

    @Test
    public void orderBookMatchBuyOrderOnAsksMarket2(){
//      Buy as much as possible, disregard remaining unfilled qty
        long remainingRequestQty = orderBook.matchBuyOrderOnAsksMarket(8);
        Assert.assertEquals(remainingRequestQty, 0);
        Assert.assertEquals(orderBook.getAsks().containsKey(1_00L), true);
        Assert.assertEquals(orderBook.getAsks().get(1_00L).getTotalQuantity(), 7);
    }

    @Test
    public void orderBookMatchSellOrderOnBidsMarket(){
//      Sell as much as possible, disregard remaining unfilled qty
        long remainingRequestQty = orderBook.matchSellOrderOnBidsMarket(20);
        Assert.assertEquals(remainingRequestQty, 0);
        Assert.assertEquals(orderBook.getBids().containsKey(2_00L), false);
    }

    @Test
    public void orderBookMatchSellOrderOnBidsMarket2(){
//      Sell as much as possible, disregard remaining unfilled qty
        long remainingRequestQty = orderBook.matchSellOrderOnBidsMarket(50);
        Assert.assertEquals(remainingRequestQty, 22);
        Assert.assertEquals(orderBook.getBids().containsKey(2_00L), false);
        Assert.assertEquals(orderBook.getBids().containsKey(1_00L), false);
    }

}
