package engine;

import command.AddLimitOrderCommand;
import command.Command;
import core.OrderBook;
import core.Side;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
        orderBook.getBids().put(1_00L,orderBook.createNewPriceLevel(1_00, 20, Side.BUY));
        orderBook.getBids().put(2_00L,orderBook.createNewPriceLevel(2_00, 10, Side.BUY));
        long seq = engine.lastProcessedSequence();
        AddLimitOrderCommand orderCommand = new AddLimitOrderCommand(seq, seq,Side.SELL,1_50L,10);
        long remainingQty = engine.addLimitOrder(orderCommand);
        Assert.assertEquals(remainingQty,0);
        Assert.assertEquals(orderBook.getBids().containsKey(2_00L),false);
    }

    @Test
    public void addLimitOrderTestPartiallyBuy(){
//        orderBook.getBids().put(1_00L,orderBook.createNewPriceLevel(1_00, 20, Side.BUY));
        orderBook.getBids().put(2_00L,orderBook.createNewPriceLevel(2_00, 10, Side.BUY));
        orderBook.getBids().put(2_00L,orderBook.createNewPriceLevel(1_50, 10, Side.BUY));
        long seq = engine.lastProcessedSequence();
        AddLimitOrderCommand orderCommand = new AddLimitOrderCommand(seq, seq,Side.SELL,1_50L,20);
        long remainingQty = engine.addLimitOrder(orderCommand);
        Assert.assertEquals(remainingQty,10);
        Assert.assertEquals(orderBook.getBids().containsKey(2_00L),false);

    }

    @Test
    public void addLimitOrderTestFullySell(){
        orderBook.getAsks().put(1_00L,orderBook.createNewPriceLevel(1_00, 20, Side.SELL));
        orderBook.getAsks().put(2_00L,orderBook.createNewPriceLevel(2_00, 10, Side.SELL));
        long seq = engine.lastProcessedSequence();
        AddLimitOrderCommand orderCommand = new AddLimitOrderCommand(seq, seq,Side.BUY,1_50L,10);
        long remainingQty = engine.addLimitOrder(orderCommand);
        Assert.assertEquals(remainingQty,0);
        Assert.assertEquals(orderBook.getAsks().get(1_00L).getTotalQuantity(),10);
    }

    @Test
    public void addLimitOrderTestPartiallySell(){
        orderBook.getAsks().put(1_00L,orderBook.createNewPriceLevel(1_00, 10, Side.SELL));
        orderBook.getAsks().put(2_00L,orderBook.createNewPriceLevel(2_00, 10, Side.SELL));
        long seq = engine.lastProcessedSequence();
        AddLimitOrderCommand orderCommand = new AddLimitOrderCommand(seq, seq,Side.BUY,1_50L,20);
        long remainingQty = engine.addLimitOrder(orderCommand);
        Assert.assertEquals(remainingQty,10);
        Assert.assertEquals(orderBook.getAsks().containsKey(1_00L),false);
    }
}
