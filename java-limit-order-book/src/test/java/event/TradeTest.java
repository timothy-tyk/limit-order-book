package event;

import org.junit.Assert;
import org.junit.Test;

public class TradeTest {
    @Test
    public void tradeEventTest(){
        Trade trade = new Trade(1,1,1,2,100,100);
        Assert.assertEquals(trade.commandSequence(),1);
        Assert.assertEquals(trade.eventSequence(),1);
    }
}
