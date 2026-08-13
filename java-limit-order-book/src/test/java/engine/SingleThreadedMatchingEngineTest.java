package engine;

import command.AddLimitOrderCommand;
import command.CancelOrderCommand;
import command.MarketOrderCommand;
import command.ModifyOrderCommand;
import core.Order;
import core.OrderBook;
import core.Side;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.beans.PropertyEditorManager;
import java.util.Date;

public class SingleThreadedMatchingEngineTest {
    SingleThreadedMatchingEngine engine;
    OrderBook orderBook;

    @Before
    public void setup(){
        engine = new SingleThreadedMatchingEngine();
        orderBook = engine.getOrderBook();
    }

    @Test
    public void addLimitOrderTestFullyBuy(){
        orderBook.getBids().put(1_00L,orderBook.createNewPriceLevel(1_00, 20, Side.BUY, 1));
        orderBook.getBids().put(2_00L,orderBook.createNewPriceLevel(2_00, 10, Side.BUY, 2));
        long seq = engine.lastProcessedSequence();
        AddLimitOrderCommand orderCommand = new AddLimitOrderCommand(seq, seq,Side.SELL,1_50L,10);
        long remainingQty = engine.addLimitOrder(orderCommand);
        Assert.assertEquals(remainingQty,0);
        Assert.assertEquals(orderBook.getBids().containsKey(2_00L),false);
    }

    @Test
    public void addLimitOrderTestPartiallyBuy(){
//        orderBook.getBids().put(1_00L,orderBook.createNewPriceLevel(1_00, 20, Side.BUY));
        orderBook.getBids().put(2_00L,orderBook.createNewPriceLevel(2_00, 10, Side.BUY, 1));
        orderBook.getBids().put(2_00L,orderBook.createNewPriceLevel(1_50, 10, Side.BUY, 2));
        long seq = engine.lastProcessedSequence();
        AddLimitOrderCommand orderCommand = new AddLimitOrderCommand(seq, seq,Side.SELL,1_50L,20);
        long remainingQty = engine.addLimitOrder(orderCommand);
        Assert.assertEquals(remainingQty,10);
        Assert.assertEquals(orderBook.getBids().containsKey(2_00L),false);

    }

    @Test
    public void addLimitOrderTestFullySell(){
        orderBook.getAsks().put(1_00L,orderBook.createNewPriceLevel(1_00, 20, Side.SELL, 1));
        orderBook.getAsks().put(2_00L,orderBook.createNewPriceLevel(2_00, 10, Side.SELL, 2));
        long seq = engine.lastProcessedSequence();
        AddLimitOrderCommand orderCommand = new AddLimitOrderCommand(seq, seq,Side.BUY,1_50L,10);
        long remainingQty = engine.addLimitOrder(orderCommand);
        Assert.assertEquals(remainingQty,0);
        Assert.assertEquals(orderBook.getAsks().get(1_00L).getTotalQuantity(),10);
    }

    @Test
    public void addLimitOrderTestPartiallySell(){
        orderBook.getAsks().put(1_00L,orderBook.createNewPriceLevel(1_00, 10, Side.SELL, 1));
        orderBook.getAsks().put(2_00L,orderBook.createNewPriceLevel(2_00, 10, Side.SELL, 2));
        long seq = engine.lastProcessedSequence();
        AddLimitOrderCommand orderCommand = new AddLimitOrderCommand(seq, seq,Side.BUY,1_50L,20);
        long remainingQty = engine.addLimitOrder(orderCommand);
        Assert.assertEquals(remainingQty,10);
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
        long remainingQty = engine.marketLimitOrder(command);
        Assert.assertEquals(remainingQty,5);
        Assert.assertEquals(orderBook.getAsks().isEmpty(), true);
    }

    @Test
    public void marketLimitOrderBuyTest2(){
        MarketOrderCommand command = new MarketOrderCommand(engine.lastProcessedSequence(), 1L,Side.BUY,25);
        long remainingQty = engine.marketLimitOrder(command);
        Assert.assertEquals(remainingQty, 25);
        Assert.assertEquals(orderBook.getBids().isEmpty(), true);
    }

    @Test
    public void marketLimitOrderSellTest(){
        orderBook.getBids().put(1_00L,orderBook.createNewPriceLevel(1_00, 20, Side.BUY,1));
        MarketOrderCommand command = new MarketOrderCommand(engine.lastProcessedSequence(), 1L,Side.SELL,25);
        long remainingQty = engine.marketLimitOrder(command);
        Assert.assertEquals(remainingQty,5);
        Assert.assertEquals(orderBook.getBids().isEmpty(), true);
    }

    @Test
    public void marketLimitOrderSellTest2(){
        MarketOrderCommand command = new MarketOrderCommand(engine.lastProcessedSequence(), 1L,Side.SELL,25);
        long remainingQty = engine.marketLimitOrder(command);
        Assert.assertEquals(remainingQty, 25);
        Assert.assertEquals(orderBook.getAsks().isEmpty(), true);
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
        AddLimitOrderCommand com2 = new AddLimitOrderCommand(2,2,Side.BUY,price,12);
        AddLimitOrderCommand com3 = new AddLimitOrderCommand(3,3,Side.SELL,price,58);
        AddLimitOrderCommand com4 = new AddLimitOrderCommand(4,4,Side.BUY,price,50);
        AddLimitOrderCommand com5 = new AddLimitOrderCommand(5,5,Side.BUY,price,40);
        engine.submitCommand(com1);
        engine.submitCommand(com2);
        engine.submitCommand(com3);
        engine.submitCommand(com4);
        engine.submitCommand(com5);
        Assert.assertEquals(engine.getOrderBook().getAsks().containsKey(price),true);
        Assert.assertEquals(engine.getOrderBook().getAsks().get(price).getTotalQuantity(),49);
        Assert.assertEquals(engine.getOrderBook().getAsks().get(price).getOrderCount(),1);
        Assert.assertEquals(engine.getOrderBook().getAsks().get(price).getOrders().getFirst().getOrderId(),3);



    }
}
