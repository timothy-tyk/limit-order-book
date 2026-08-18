package workload;

import benchmark.LatencyRecorder;
import benchmark.WorkloadProfile;
import core.OrderBook;
import engine.SingleThreadedMatchingEngine;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import utils.LiveOrderTracker;
import validation.EventRecorder;

public class RandomWorkloadGeneratorTest {
    RandomWorkloadGenerator randomWorkloadGenerator;
    @Before
    public void setup(){
        WorkloadProfile testProfile = new WorkloadProfile("Test",1,100_000,50,25,25,15);
        randomWorkloadGenerator = new RandomWorkloadGenerator(
                testProfile,
                new SingleThreadedMatchingEngine(new EventRecorder(false), new LatencyRecorder(1000), new LiveOrderTracker()));
    }

    @Test
    public void generateTest(){
        randomWorkloadGenerator.generate(false);
        Assert.assertEquals(randomWorkloadGenerator.getLastProcessedSequence(),100_000);
    }
}
