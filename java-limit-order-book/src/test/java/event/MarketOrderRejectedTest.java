package event;

import core.Side;
import org.junit.Assert;
import org.junit.Test;

public class MarketOrderRejectedTest {
    @Test
    public void marketOrderRejectedTest(){
        MarketOrderRejected marketRejected = new MarketOrderRejected(1,1,2);
        Assert.assertEquals(marketRejected.commandSequence(),1);
        Assert.assertEquals(marketRejected.eventSequence(),1);
    }
}
