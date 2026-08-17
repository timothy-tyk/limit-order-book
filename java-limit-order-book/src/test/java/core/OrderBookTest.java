package core;

import event.TradeDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class OrderBookTest {
    OrderBook orderBook;

    @Before
    public void setup(){
        orderBook = new OrderBook();
        TreeMap<Long, PriceLevel> asks = new TreeMap<>();

        Order order1 = new Order(1L,1L,Side.SELL,1_00,10, new Date().getTime());
        Order order2 = new Order(2L,1L,Side.SELL, 1_00,5,new Date().getTime());
        LinkedHashMap<Long,Order> sellMap1L = new LinkedHashMap<>();
        sellMap1L.put(1L,order1);
        sellMap1L.put(2L,order2);
        PriceLevel priceLevelSell1L = new PriceLevel(sellMap1L,order1.getQuantity()+order2.getQuantity(),sellMap1L.size());

        Order order3 = new Order(3L,1L,Side.SELL,2_00,6, new Date().getTime());
        Order order4 = new Order(4L,1L,Side.SELL, 2_00,5,new Date().getTime());
        LinkedHashMap<Long,Order> sellMap2L = new LinkedHashMap<>();
        sellMap2L.put(3L,order3);
        sellMap2L.put(4L,order4);
        PriceLevel priceLevelSell2L = new PriceLevel(sellMap2L,order3.getQuantity()+order4.getQuantity(),sellMap2L.size());
        asks.put(1_00L, priceLevelSell1L);
        asks.put(2_00L, priceLevelSell2L);
        orderBook.setAsks(asks);

        TreeMap<Long, PriceLevel> bids = new TreeMap<>();

        Order order5 = new Order(5L,1L,Side.BUY,1_00,10,new Date().getTime());
        Order order6 = new Order(6L,1L,Side.BUY, 1_00,5,new Date().getTime());
        LinkedHashMap<Long,Order> buyMap1L = new LinkedHashMap<>();
        buyMap1L.put(5L,order5);
        buyMap1L.put(6L,order6);
        PriceLevel priceLevelBuy1L = new PriceLevel(buyMap1L,order5.getQuantity()+order6.getQuantity(),buyMap1L.size());

        Order order7 = new Order(7L,1L,Side.BUY,2_00,8,new Date().getTime());
        Order order8 = new Order(8L,1L,Side.BUY, 2_00,5,new Date().getTime());
        LinkedHashMap<Long,Order> buyMap2L = new LinkedHashMap<>();
        buyMap2L.put(7L,order7);
        buyMap2L.put(8L,order8);
        PriceLevel priceLevelBuy2L = new PriceLevel(buyMap2L,order7.getQuantity()+order8.getQuantity(),buyMap2L.size());
        bids.put(1_00L, priceLevelBuy1L);
        bids.put(2_00L, priceLevelBuy2L);
        orderBook.setBids(bids);
    }

    @Test
    public void orderBookMatchBuyOrderOnAsks(){
//      Buy as much as possible, remaining buy qty add to PriceLevel
        Queue<TradeDTO> tradeDTOs = orderBook.matchBuyOrderOnAsks(1_50, 20,9);
        for(TradeDTO tradeDTO:tradeDTOs){
            System.out.println(tradeDTO.toString());
        }
        Assert.assertEquals(tradeDTOs.size(), 2);
        Assert.assertEquals(orderBook.getBids().containsKey(1_50L), true);
    }

    @Test
    public void orderBookMatchBuyOrderOnAsks2(){
//      Buy as much as possible, remaining buy qty add to PriceLevel
        Queue<TradeDTO> tradeDTOs = orderBook.matchBuyOrderOnAsks(2_00, 20, 9);
        for(TradeDTO tradeDTO:tradeDTOs){
            System.out.println(tradeDTO.toString());
        }
        Assert.assertEquals(tradeDTOs.size(), 3);
        Assert.assertEquals(orderBook.getBids().containsKey(1_50L), false);
    }

    @Test
    public void orderBookUnableToMatchBuyOrderOnAsks(){
//        Cant buy, buyPrice too low
        Queue<TradeDTO> tradeDTOs = orderBook.matchBuyOrderOnAsks(50, 20, 9);
        for(TradeDTO tradeDTO:tradeDTOs){
            System.out.println(tradeDTO.toString());
        }
        Assert.assertEquals(orderBook.getBids().containsKey(50L),true);
        Assert.assertEquals(tradeDTOs.size(), 0);
    }

    @Test
    public void orderBookMatchSellOrderOnBids(){
//        Sell as much as possible, remaining sell qty add to PriceLevel
        Queue<TradeDTO> tradeDTOs = orderBook.matchSellOrderOnBids(1_50, 20, 9);
        for(TradeDTO tradeDTO:tradeDTOs){
            System.out.println(tradeDTO.toString());
        }
        Assert.assertEquals(tradeDTOs.size(), 2);
        Assert.assertEquals(orderBook.getAsks().containsKey(1_50L), true);
    }

    @Test
    public void orderBookMatchSellOrderOnBids2(){
//        Sell as much as possible, remaining sell qty add to PriceLevel
        Queue<TradeDTO> tradeDTOs = orderBook.matchSellOrderOnBids(1_00, 20, 9);
        for(TradeDTO tradeDTO:tradeDTOs){
            System.out.println(tradeDTO.toString());
        }
        Assert.assertEquals(tradeDTOs.size(), 3);
        Assert.assertEquals(orderBook.getBids().containsKey(2_00L), false);
        Assert.assertEquals(orderBook.getBids().get(1_00L).getTotalQuantity(), 8);
    }

    @Test
    public void orderBookUnableToMatchSellOrderOnBids(){
        Queue<TradeDTO> tradeDTOs = orderBook.matchSellOrderOnBids(500, 20, 9);
        for(TradeDTO tradeDTO:tradeDTOs){
            System.out.println(tradeDTO.toString());
        }
        Assert.assertEquals(orderBook.getAsks().containsKey(500L),true);
        Assert.assertEquals(orderBook.getAsks().get(500L).getTotalQuantity(),20);
        Assert.assertEquals(tradeDTOs.size(), 0);
    }

    @Test
    public void orderBookMatchBuyOrderOnAsksMarket(){
//      Buy as much as possible, disregard remaining unfilled qty
        Queue<TradeDTO> tradeDTOs = orderBook.matchBuyOrderOnAsksMarket(20,9);
        for(TradeDTO tradeDTO:tradeDTOs){
            System.out.println(tradeDTO.toString());
        }
        Assert.assertEquals(tradeDTOs.size(), 3);
        Assert.assertEquals(orderBook.getAsks().containsKey(1_00L), false);
        Assert.assertEquals(orderBook.getAsks().get(2_00L).getTotalQuantity(), 6);
    }

    @Test
    public void orderBookMatchBuyOrderOnAsksMarket2(){
//      Buy as much as possible, disregard remaining unfilled qty
        Queue<TradeDTO> tradeDTOs = orderBook.matchBuyOrderOnAsksMarket(8,9);
        for(TradeDTO tradeDTO:tradeDTOs){
            System.out.println(tradeDTO.toString());
        }
        Assert.assertEquals(tradeDTOs.size(), 1);
        Assert.assertEquals(orderBook.getAsks().containsKey(1_00L), true);
        Assert.assertEquals(orderBook.getAsks().get(1_00L).getTotalQuantity(), 7);
    }

    @Test
    public void orderBookMatchSellOrderOnBidsMarket(){
//      Sell as much as possible, disregard remaining unfilled qty
        Queue<TradeDTO> tradeDTOs = orderBook.matchSellOrderOnBidsMarket(20,9);
        for(TradeDTO tradeDTO:tradeDTOs){
            System.out.println(tradeDTO.toString());
        }
        Assert.assertEquals(tradeDTOs.size(), 3);
        Assert.assertEquals(orderBook.getBids().containsKey(2_00L), false);
    }

    @Test
    public void orderBookMatchSellOrderOnBidsMarket2(){
//      Sell as much as possible, disregard remaining unfilled qty
        Queue<TradeDTO> tradeDTOs = orderBook.matchSellOrderOnBidsMarket(50,9);
        for(TradeDTO tradeDTO:tradeDTOs){
            System.out.println(tradeDTO.toString());
        }
        Assert.assertEquals(tradeDTOs.size(), 4);
        Assert.assertEquals(orderBook.getBids().containsKey(2_00L), false);
        Assert.assertEquals(orderBook.getBids().containsKey(1_00L), false);
    }

}
