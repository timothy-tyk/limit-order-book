package event;

import core.Side;
import org.junit.Assert;
import org.junit.Test;

public class OrderAcceptedTest {
    @Test
    public void orderAcceptedTest(){
        OrderAccepted orderAccepted = new OrderAccepted(1,1,2, Side.BUY,100,100);
        Assert.assertEquals(orderAccepted.commandSequence(),1);
        Assert.assertEquals(orderAccepted.eventSequence(),1);
    }
}
