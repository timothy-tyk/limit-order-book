package event;

import core.Side;
import org.junit.Assert;
import org.junit.Test;

public class OrderCancelledTest {
    @Test
    public void orderCancelledTest(){
        OrderCancelled orderCancelled = new OrderCancelled(1,1,2);
        Assert.assertEquals(orderCancelled.commandSequence(),1);
        Assert.assertEquals(orderCancelled.eventSequence(),1);
    }
}
