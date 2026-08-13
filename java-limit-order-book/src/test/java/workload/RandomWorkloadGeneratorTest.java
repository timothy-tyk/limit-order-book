package workload;

import core.OrderBook;
import engine.SingleThreadedMatchingEngine;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RandomWorkloadGeneratorTest {
    RandomWorkloadGenerator randomWorkloadGenerator;
    @Before
    public void setup(){
        randomWorkloadGenerator = new RandomWorkloadGenerator(1, new SingleThreadedMatchingEngine());
    }

    @Test
    public void generateTest(){
        long liveSize = randomWorkloadGenerator.generate(100000);
        Assert.assertEquals(liveSize>0,true);
    }
}
