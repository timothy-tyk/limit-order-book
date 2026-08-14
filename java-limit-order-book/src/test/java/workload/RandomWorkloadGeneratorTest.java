package workload;

import core.OrderBook;
import engine.SingleThreadedMatchingEngine;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import validation.EventRecorder;

public class RandomWorkloadGeneratorTest {
    RandomWorkloadGenerator randomWorkloadGenerator;
    @Before
    public void setup(){
        randomWorkloadGenerator = new RandomWorkloadGenerator(1, new SingleThreadedMatchingEngine(new EventRecorder(false)));
    }

    @Test
    public void generateTest(){
        long liveSize = randomWorkloadGenerator.generate(100_000);
        Assert.assertEquals(liveSize>0,true);
    }
}
