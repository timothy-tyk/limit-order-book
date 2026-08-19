package engine;

import benchmark.LatencyRecorder;
import command.AddLimitOrderCommand;
import command.CancelOrderCommand;
import command.MarketOrderCommand;
import command.ModifyOrderCommand;
import core.Order;
import core.OrderBook;
import core.Side;
import engine.concurrent.SynchronizedMatchingEngine;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import utils.LiveOrderTracker;
import validation.EventRecorder;

import java.util.Date;

public class SynchronizedMatchingEngineTest {
    SynchronizedMatchingEngine engine;
    OrderBook orderBook;

    @Before
    public void setup(){
        engine = new SynchronizedMatchingEngine(
                new EventRecorder(false),
                new LatencyRecorder(100),
                new LiveOrderTracker()
                );
        orderBook = engine.getOrderBook();
    }


    @Test
    public void addLimitOrderTestFullyBuy(){
        orderBook.getBids().put(1_00L,orderBook.createNewPriceLevel(1_00, 20, Side.BUY, 1));
        orderBook.getBids().put(2_00L,orderBook.createNewPriceLevel(2_00, 10, Side.BUY, 2));
        long seq = engine.lastProcessedSequence();
        AddLimitOrderCommand orderCommand = new AddLimitOrderCommand(seq, seq,Side.SELL,1_50L,10);
        engine.addLimitOrder(orderCommand);
//        Assert.assertEquals(remainingQty,0);
        Assert.assertEquals(orderBook.getBids().containsKey(2_00L),false);
    }

    @Test
    public void addLimitOrderTestPartiallyBuy(){
//        orderBook.getBids().put(1_00L,orderBook.createNewPriceLevel(1_00, 20, Side.BUY));
        orderBook.getBids().put(2_00L,orderBook.createNewPriceLevel(2_00, 10, Side.BUY, 1));
        orderBook.getBids().put(2_00L,orderBook.createNewPriceLevel(1_50, 10, Side.BUY, 2));
        long seq = engine.lastProcessedSequence();
        AddLimitOrderCommand orderCommand = new AddLimitOrderCommand(seq, seq,Side.SELL,1_50L,20);
        engine.addLimitOrder(orderCommand);
//        Assert.assertEquals(remainingQty,10);
        Assert.assertEquals(orderBook.getBids().containsKey(2_00L),false);

    }

    @Test
    public void addLimitOrderTestFullySell(){
        orderBook.getAsks().put(1_00L,orderBook.createNewPriceLevel(1_00, 20, Side.SELL, 1));
        orderBook.getAsks().put(2_00L,orderBook.createNewPriceLevel(2_00, 10, Side.SELL, 2));
        long seq = engine.lastProcessedSequence();
        AddLimitOrderCommand orderCommand = new AddLimitOrderCommand(seq, seq,Side.BUY,1_50L,10);
        engine.addLimitOrder(orderCommand);
//        Assert.assertEquals(remainingQty,0);
        Assert.assertEquals(orderBook.getAsks().get(1_00L).getTotalQuantity(),10);
    }

    @Test
    public void addLimitOrderTestPartiallySell(){
        orderBook.getAsks().put(1_00L,orderBook.createNewPriceLevel(1_00, 10, Side.SELL, 1));
        orderBook.getAsks().put(2_00L,orderBook.createNewPriceLevel(2_00, 10, Side.SELL, 2));
        long seq = engine.lastProcessedSequence();
        AddLimitOrderCommand orderCommand = new AddLimitOrderCommand(seq, seq,Side.BUY,1_50L,20);
        engine.addLimitOrder(orderCommand);
//        Assert.assertEquals(remainingQty,10);
        Assert.assertEquals(orderBook.getAsks().containsKey(1_00L),false);
    }

    @Test
    public void cancelLimitOrderTest(){
        orderBook.getAsks().put(1_00L,orderBook.createNewPriceLevel(1_00, 10, Side.SELL,1));
        CancelOrderCommand command = new CancelOrderCommand(engine.lastProcessedSequence(), 1L);
        engine.cancelLimitOrder(command);
        Assert.assertEquals(orderBook.getAsks().containsKey(1_00L), false);
        Assert.assertEquals(orderBook.getAsks().size(),0);
        Assert.assertEquals(orderBook.getOrdersById().containsKey(1L), false);
        Assert.assertEquals(orderBook.getOrdersById().size(),0);
    }

    @Test
    public void cancelLimitOrderTest2(){
        orderBook.getAsks().put(1_00L,orderBook.createNewPriceLevel(1_00, 10, Side.SELL,1));
        orderBook.addOrder(new Order(2L, 1L,Side.SELL,1_00L,10,new Date().getTime()));
        CancelOrderCommand command = new CancelOrderCommand(engine.lastProcessedSequence(), 1L);
        engine.cancelLimitOrder(command);
        Assert.assertEquals(orderBook.getAsks().containsKey(1_00L), true);
        Assert.assertEquals(orderBook.getAsks().size(),1);
        Assert.assertEquals(orderBook.getOrdersById().containsKey(1L), false);
        Assert.assertEquals(orderBook.getOrdersById().size(),1);
    }

    @Test
    public void modifyLimitOrderTest(){
        orderBook.getAsks().put(1_00L,orderBook.createNewPriceLevel(1_00, 10, Side.SELL,1));
        ModifyOrderCommand command = new ModifyOrderCommand(engine.lastProcessedSequence(),1L,Side.SELL,2_00,20);
        engine.modifyLimitOrder(command);
        Assert.assertEquals(orderBook.getAsks().containsKey(1_00L), false);
        Assert.assertEquals(orderBook.getAsks().containsKey(2_00L), true);
    }

    @Test
    public void modifyLimitOrderTest2(){
        orderBook.getAsks().put(1_00L,orderBook.createNewPriceLevel(1_00, 10, Side.SELL,1));
        ModifyOrderCommand command = new ModifyOrderCommand(engine.lastProcessedSequence(),1L,Side.BUY,2_00,20);
        engine.modifyLimitOrder(command);
        Assert.assertEquals(orderBook.getAsks().containsKey(1_00L), false);
        Assert.assertEquals(orderBook.getBids().containsKey(2_00L), true);
    }

    @Test
    public void marketLimitOrderBuyTest(){
        orderBook.getAsks().put(1_00L,orderBook.createNewPriceLevel(1_00, 20, Side.SELL,1));
        MarketOrderCommand command = new MarketOrderCommand(engine.lastProcessedSequence(), 1L,Side.BUY,25);
        engine.marketLimitOrder(command);
//        Assert.assertEquals(remainingQty,5);
        Assert.assertEquals(orderBook.getAsks().isEmpty(), true);
    }

    @Test
    public void marketLimitOrderBuyTest2() {
        MarketOrderCommand command = new MarketOrderCommand(engine.lastProcessedSequence(), 1L, Side.BUY, 25);
        engine.marketLimitOrder(command);
//        Assert.assertEquals(remainingQty, 25);
        Assert.assertEquals(orderBook.getBids().isEmpty(), true);
    }

    @Test
    public void marketLimitOrderSellTest(){
        orderBook.getBids().put(1_00L,orderBook.createNewPriceLevel(1_00, 20, Side.BUY,1));
        MarketOrderCommand command = new MarketOrderCommand(engine.lastProcessedSequence(), 1L,Side.SELL,25);
        engine.marketLimitOrder(command);
//        Assert.assertEquals(remainingQty,5);
        Assert.assertEquals(orderBook.getBids().isEmpty(), true);
    }

    @Test
    public void marketLimitOrderSellTest2(){
        MarketOrderCommand command = new MarketOrderCommand(engine.lastProcessedSequence(), 1L,Side.SELL,25);
        engine.marketLimitOrder(command);
//        Assert.assertEquals(remainingQty, 25);
        Assert.assertEquals(orderBook.getAsks().isEmpty(), true);
    }

    @Test
    public void limitOrderRejectedTest(){
        orderBook.getBids().put(1_00L,orderBook.createNewPriceLevel(1_00, 20, Side.BUY, 1));
        orderBook.getBids().put(2_00L,orderBook.createNewPriceLevel(2_00, 10, Side.BUY, 2));
        long seq = engine.lastProcessedSequence();
        AddLimitOrderCommand orderCommand = new AddLimitOrderCommand(seq, seq,Side.SELL,1_50L,-10);
        engine.addLimitOrder(orderCommand);
        Assert.assertEquals(orderBook.getAsks().containsKey(1_50L), false);
    }

    @Test
    public void marketLimitOrderRejectedTest(){
        orderBook.getAsks().put(1_00L,orderBook.createNewPriceLevel(1_00, 20, Side.SELL,1));
        MarketOrderCommand command = new MarketOrderCommand(engine.lastProcessedSequence(), 1L,Side.BUY,0);
        engine.marketLimitOrder(command);
        Assert.assertEquals(orderBook.getAsks().isEmpty(), false);
    }


    @Test
    public void sanityTest(){
        /*
        SELL - 99997 - 93
        BUY: 99998 12
        SELL - 99997 - 58
        BUY: 100007 50
        BUY: 100005 40

        result should return
            - totalQty = 49
            - 1 orders
                - remaining qty = 49
                - orderId = 3 (since it is the 3rd command sent)

         */
        long price = 99997L;

        AddLimitOrderCommand com1 = new AddLimitOrderCommand(1,1,Side.SELL,price,93);
        AddLimitOrderCommand com2 = new AddLimitOrderCommand(2,2,Side.BUY,99998L,12);
        AddLimitOrderCommand com3 = new AddLimitOrderCommand(3,3,Side.SELL,price,58);
        AddLimitOrderCommand com4 = new AddLimitOrderCommand(4,4,Side.BUY,100007L,50);
        AddLimitOrderCommand com5 = new AddLimitOrderCommand(5,5,Side.BUY,100005L,40);
        engine.submitCommand(com1);
        engine.submitCommand(com2);
        engine.submitCommand(com3);
        engine.submitCommand(com4);
        engine.submitCommand(com5);
        Assert.assertEquals(engine.getOrderBook().getAsks().containsKey(price),true);
        Assert.assertEquals(engine.getOrderBook().getAsks().get(price).getTotalQuantity(),49);
        Assert.assertEquals(engine.getOrderBook().getAsks().get(price).getOrderCount(),1);
        Assert.assertEquals(engine.getOrderBook().getAsks().get(price).getOrders().pollFirstEntry().getValue().getOrderId(),3);
    }

    @Test
    public void sanityTest2(){
        /*
        SELL - 99997 - 93
        BUY: 99998 12
        SELL - 99997 - 58
        CANCEL - 99997 - id=1
        BUY: 100007 50
        BUY: 100005 40

        result should return
            - Asks@99997L totalQty = 0, no orders, no pricelevel exists
            - Bids@100005L totalQty = 32
            - 1 orders
                - remaining qty = 32
                - orderId = 5 (since it is the 3rd command sent)

         */
        long price = 99997L;

        AddLimitOrderCommand com1 = new AddLimitOrderCommand(1,1,Side.SELL,price,93);
        AddLimitOrderCommand com2 = new AddLimitOrderCommand(2,2,Side.BUY,99998L,12);
        AddLimitOrderCommand com3 = new AddLimitOrderCommand(3,3,Side.SELL,price,58);
        CancelOrderCommand com4 = new CancelOrderCommand(4,1);
        AddLimitOrderCommand com5 = new AddLimitOrderCommand(5,4,Side.BUY,100007L,50);
        AddLimitOrderCommand com6 = new AddLimitOrderCommand(6,5,Side.BUY,100005L,40);
        engine.submitCommand(com1);
        engine.submitCommand(com2);
        engine.submitCommand(com3);
        engine.submitCommand(com4);
        engine.submitCommand(com5);
        engine.submitCommand(com6);
        Assert.assertEquals(engine.getOrderBook().getAsks().containsKey(price),false);
        Assert.assertEquals(engine.getOrderBook().getBids().containsKey(100005L),true);
        Assert.assertEquals(engine.getOrderBook().getBids().get(100005L).getOrderCount(),1);
        Assert.assertEquals(engine.getOrderBook().getBids().get(100005L).getTotalQuantity(),32);
        Assert.assertEquals(engine.getOrderBook().getBids().get(100005L).getOrders().pollFirstEntry().getValue().getOrderId(),5);
    }

}
