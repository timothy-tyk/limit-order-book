package event;

import org.junit.Assert;
import org.junit.Test;

public class OrderRejectedTest {
    @Test
    public void orderRejectedTest(){
        OrderRejected rejected = new OrderRejected(1,1,2);
        Assert.assertEquals(rejected.commandSequence(),1);
        Assert.assertEquals(rejected.eventSequence(),1);
    }
}
