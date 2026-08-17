package workload;

import benchmark.LatencyRecorder;
import benchmark.WorkloadProfile;
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
        WorkloadProfile testProfile = new WorkloadProfile("Test",1,100_000,50,25,25,15);
        randomWorkloadGenerator = new RandomWorkloadGenerator(testProfile, new SingleThreadedMatchingEngine(new EventRecorder(false), new LatencyRecorder(1000)));
    }

    @Test
    public void generateTest(){
        long liveSize = randomWorkloadGenerator.generate();
        Assert.assertEquals(liveSize>0,true);
        Assert.assertEquals(randomWorkloadGenerator.getLastProcessedSequence(),100_000);
    }
}
