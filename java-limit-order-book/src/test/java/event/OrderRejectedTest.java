package event;

import org.junit.Assert;
import org.junit.Test;
import validation.OrderRejectedReason;

public class OrderRejectedTest {
    @Test
    public void orderRejectedTest(){
        OrderRejected rejected = new OrderRejected(1,1,2, OrderRejectedReason.UNKNOWN_ORDER);
        Assert.assertEquals(rejected.commandSequence(),1);
        Assert.assertEquals(rejected.eventSequence(),1);
    }
}
