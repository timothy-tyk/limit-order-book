package event;

import core.Side;
import org.junit.Assert;
import org.junit.Test;

public class MarketOrderAcceptedTest {
    @Test
    public void marketOrderAcceptedTest(){
        MarketOrderAccepted marketAccepted = new MarketOrderAccepted(1,1,2, Side.BUY,100);
        Assert.assertEquals(marketAccepted.commandSequence(),1);
        Assert.assertEquals(marketAccepted.eventSequence(),1);
    }
}
