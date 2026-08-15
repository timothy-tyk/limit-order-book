package event;

import core.Side;
import org.junit.Assert;
import org.junit.Test;

public class OrderModifiedTest {
    @Test
    public void orderModifiedTest(){
        OrderModified orderModified = new OrderModified(1,1,2, Side.BUY,100,100);
        Assert.assertEquals(orderModified.commandSequence(),1);
        Assert.assertEquals(orderModified.eventSequence(),1);
    }
}
