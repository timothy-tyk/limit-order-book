package engine;

import benchmark.LatencyRecorder;
import command.AddLimitOrderCommand;
import command.CancelOrderCommand;
import command.MarketOrderCommand;
import command.ModifyOrderCommand;
import core.Order;
import core.OrderBook;
import core.Side;
import engine.singlewriter.SingleWriterMatchingEngine;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import utils.Constants;
import utils.LiveOrderTracker;
import validation.EventRecorder;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

public class SingleWriterMatchingEngineTest {
    SingleWriterMatchingEngine engine;
    OrderBook orderBook;

    @Before
    public void setup(){
        engine = new SingleWriterMatchingEngine(
                new EventRecorder(false),
                new LatencyRecorder(10000),
                new LiveOrderTracker(),
                Constants.QUEUE_CAPACITY
        );
        orderBook = engine.getOrderBook();
    }

    @Test
    public void addLimitOrderTestFullyBuy() throws InterruptedException {
        orderBook.getBids().put(1_00L,orderBook.createNewPriceLevel(1_00, 20, Side.BUY, 1));
        orderBook.getBids().put(2_00L,orderBook.createNewPriceLevel(2_00, 10, Side.BUY, 2));
        long seq = engine.lastProcessedSequence();
        AddLimitOrderCommand orderCommand = new AddLimitOrderCommand(seq, seq,Side.SELL,1_50L,10);

        engine.start();
        engine.submitCommand(orderCommand);
        engine.awaitQueueCompletion(100);
        Assert.assertEquals(orderBook.getBids().containsKey(2_00L),false);
        engine.stop();
        Assert.assertEquals(engine.getSubmittedCommands().get(), 1L);
        Assert.assertEquals(engine.getProcessedCommands().get(),1L);
    }

    @Test
    public void addLimitOrderTestPartiallyBuy() throws InterruptedException {
//        orderBook.getBids().put(1_00L,orderBook.createNewPriceLevel(1_00, 20, Side.BUY));
        orderBook.getBids().put(2_00L,orderBook.createNewPriceLevel(2_00, 10, Side.BUY, 1));
        orderBook.getBids().put(2_00L,orderBook.createNewPriceLevel(1_50, 10, Side.BUY, 2));
        long seq = engine.lastProcessedSequence();
        AddLimitOrderCommand orderCommand = new AddLimitOrderCommand(seq, seq,Side.SELL,1_50L,20);
        engine.start();
        engine.submitCommand(orderCommand);
        engine.awaitQueueCompletion(100);
        Assert.assertEquals(orderBook.getBids().containsKey(2_00L),false);
        engine.stop();
        Assert.assertEquals(engine.getSubmittedCommands().get(),1L);
        Assert.assertEquals(engine.getProcessedCommands().get(),1L);
    }

    @Test
    public void addLimitOrderTestFullySell() throws InterruptedException {
        orderBook.getAsks().put(1_00L,orderBook.createNewPriceLevel(1_00, 20, Side.SELL, 1));
        orderBook.getAsks().put(2_00L,orderBook.createNewPriceLevel(2_00, 10, Side.SELL, 2));
        long seq = engine.lastProcessedSequence();
        AddLimitOrderCommand orderCommand = new AddLimitOrderCommand(seq, seq,Side.BUY,1_50L,10);
        engine.start();
        engine.submitCommand(orderCommand);
        engine.awaitQueueCompletion(100);
        Assert.assertEquals(orderBook.getAsks().get(1_00L).getTotalQuantity(),10);
        engine.stop();
    }

    @Test
    public void addLimitOrderTestPartiallySell() throws InterruptedException {
        orderBook.getAsks().put(1_00L,orderBook.createNewPriceLevel(1_00, 10, Side.SELL, 1));
        orderBook.getAsks().put(2_00L,orderBook.createNewPriceLevel(2_00, 10, Side.SELL, 2));
        long seq = engine.lastProcessedSequence();
        AddLimitOrderCommand orderCommand = new AddLimitOrderCommand(seq, seq,Side.BUY,1_50L,20);
        engine.start();
        engine.submitCommand(orderCommand);
        engine.awaitQueueCompletion(100);
        Assert.assertEquals(orderBook.getAsks().containsKey(1_00L),false);
        engine.stop();
    }

    @Test
    public void cancelLimitOrderTest() throws InterruptedException {
        orderBook.getAsks().put(1_00L,orderBook.createNewPriceLevel(1_00, 10, Side.SELL,1));
        CancelOrderCommand orderCommand = new CancelOrderCommand(engine.lastProcessedSequence(), 1L);
        engine.start();
        engine.submitCommand(orderCommand);
        engine.awaitQueueCompletion(100);
        Assert.assertEquals(orderBook.getAsks().containsKey(1_00L), false);
        Assert.assertEquals(orderBook.getAsks().size(),0);
        Assert.assertEquals(orderBook.getOrdersById().containsKey(1L), false);
        Assert.assertEquals(orderBook.getOrdersById().size(),0);
        engine.stop();
    }

    @Test
    public void cancelLimitOrderTest2() throws InterruptedException {
        orderBook.getAsks().put(1_00L,orderBook.createNewPriceLevel(1_00, 10, Side.SELL,1));
        orderBook.addOrder(new Order(2L, 1L,Side.SELL,1_00L,10,new Date().getTime()));
        CancelOrderCommand orderCommand = new CancelOrderCommand(engine.lastProcessedSequence(), 1L);
        engine.start();
        engine.submitCommand(orderCommand);
        engine.awaitQueueCompletion(100);
        Assert.assertEquals(orderBook.getAsks().containsKey(1_00L), true);
        Assert.assertEquals(orderBook.getAsks().size(),1);
        Assert.assertEquals(orderBook.getOrdersById().containsKey(1L), false);
        Assert.assertEquals(orderBook.getOrdersById().size(),1);
        engine.stop();
    }

    @Test
    public void modifyLimitOrderTest() throws InterruptedException {
        orderBook.getAsks().put(1_00L,orderBook.createNewPriceLevel(1_00, 10, Side.SELL,1));
        ModifyOrderCommand orderCommand = new ModifyOrderCommand(engine.lastProcessedSequence(),1L,Side.SELL,2_00,20);
        engine.start();
        engine.submitCommand(orderCommand);
        engine.awaitQueueCompletion(100);
        Assert.assertEquals(orderBook.getAsks().containsKey(1_00L), false);
        Assert.assertEquals(orderBook.getAsks().containsKey(2_00L), true);
        engine.stop();
    }

    @Test
    public void modifyLimitOrderTest2() throws InterruptedException {
        orderBook.getAsks().put(1_00L,orderBook.createNewPriceLevel(1_00, 10, Side.SELL,1));
        ModifyOrderCommand orderCommand = new ModifyOrderCommand(engine.lastProcessedSequence(),1L,Side.BUY,2_00,20);
        engine.start();
        engine.submitCommand(orderCommand);
        engine.awaitQueueCompletion(100);
        Assert.assertEquals(orderBook.getAsks().containsKey(1_00L), false);
        Assert.assertEquals(orderBook.getBids().containsKey(2_00L), true);
        engine.stop();
    }

    @Test
    public void marketLimitOrderBuyTest() throws InterruptedException {
        orderBook.getAsks().put(1_00L,orderBook.createNewPriceLevel(1_00, 20, Side.SELL,1));
        MarketOrderCommand orderCommand = new MarketOrderCommand(engine.lastProcessedSequence(), 1L,Side.BUY,25);
        engine.start();
        engine.submitCommand(orderCommand);
        engine.awaitQueueCompletion(100);
        Assert.assertEquals(orderBook.getAsks().isEmpty(), true);
        engine.stop();
    }

    @Test
    public void marketLimitOrderBuyTest2() throws InterruptedException {
        MarketOrderCommand orderCommand = new MarketOrderCommand(engine.lastProcessedSequence(), 1L,Side.BUY,25);
        engine.start();
        engine.submitCommand(orderCommand);
        engine.awaitQueueCompletion(100);
        Assert.assertEquals(orderBook.getBids().isEmpty(), true);
        engine.stop();
    }

    @Test
    public void marketLimitOrderSellTest() throws InterruptedException {
        orderBook.getBids().put(1_00L,orderBook.createNewPriceLevel(1_00, 20, Side.BUY,1));
        MarketOrderCommand orderCommand = new MarketOrderCommand(engine.lastProcessedSequence(), 1L,Side.SELL,25);
        engine.start();
        engine.submitCommand(orderCommand);
        engine.awaitQueueCompletion(100);
        Assert.assertEquals(orderBook.getBids().isEmpty(), true);
        engine.stop();
    }

    @Test
    public void marketLimitOrderSellTest2() throws InterruptedException {
        MarketOrderCommand orderCommand = new MarketOrderCommand(engine.lastProcessedSequence(), 1L,Side.SELL,25);
        engine.start();
        engine.submitCommand(orderCommand);
        engine.awaitQueueCompletion(100);
        Assert.assertEquals(orderBook.getAsks().isEmpty(), true);
        engine.stop();
    }

    @Test
    public void limitOrderRejectedTest() throws InterruptedException {
        orderBook.getBids().put(1_00L,orderBook.createNewPriceLevel(1_00, 20, Side.BUY, 1));
        orderBook.getBids().put(2_00L,orderBook.createNewPriceLevel(2_00, 10, Side.BUY, 2));
        long seq = engine.lastProcessedSequence();
        AddLimitOrderCommand orderCommand = new AddLimitOrderCommand(seq, seq,Side.SELL,1_50L,-10);
        engine.start();
        engine.submitCommand(orderCommand);
        engine.awaitQueueCompletion(100);
        Assert.assertEquals(orderBook.getAsks().containsKey(1_50L), false);
        engine.stop();
        engine.showEventSummary();
        engine.showLatencySummary();
    }

    @Test
    public void marketLimitOrderRejectedTest() throws InterruptedException {
        orderBook.getAsks().put(1_00L,orderBook.createNewPriceLevel(1_00, 20, Side.SELL,1));
        MarketOrderCommand orderCommand = new MarketOrderCommand(engine.lastProcessedSequence(), 1L,Side.BUY,0);
        engine.start();
        engine.submitCommand(orderCommand);
        engine.awaitQueueCompletion(100);
        Assert.assertEquals(orderBook.getAsks().isEmpty(), false);
        engine.stop();
    }

    @Test
    public void submitCommandNotRunningTest(){
        orderBook.getAsks().put(1_00L,orderBook.createNewPriceLevel(1_00, 20, Side.SELL,1));
        MarketOrderCommand orderCommand = new MarketOrderCommand(engine.lastProcessedSequence(), 1L,Side.BUY,0);
        Assert.assertThrows(IllegalStateException.class,()->engine.submitCommand(orderCommand));
    }

    @Test
    public void submitCommandInterruptedExceptionTest(){
        orderBook.getAsks().put(1_00L,orderBook.createNewPriceLevel(1_00, 20, Side.SELL,1));
        MarketOrderCommand orderCommand = new MarketOrderCommand(engine.lastProcessedSequence(), 1L,Side.BUY,0);
        engine.start();
        Assert.assertThrows(IllegalStateException.class,()->{
            Thread.currentThread().interrupt();
            engine.submitCommand(orderCommand);
        });
    }
}
