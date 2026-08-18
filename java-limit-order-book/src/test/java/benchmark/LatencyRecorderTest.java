package benchmark;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LatencyRecorderTest {
    LatencyRecorder recorder;
    @Before
    public void setup(){
        int capacity = 100;
        recorder = new LatencyRecorder(capacity);
        for(int i=1;i<capacity+1;i++){
            recorder.record(i);
        };
    }

    @Test
    public void countTest(){
        Assert.assertEquals(recorder.count(),100);
    }

    @Test
    public void averageNanosTest(){
        Assert.assertEquals(recorder.averageNanos(),50.5,0);
    }

    @Test
    public void percentileTest50(){
        Assert.assertEquals(recorder.percentile(50), 50);
    }

    @Test
    public void percentileTest99(){
        Assert.assertEquals(recorder.percentile(99), 99);
    }

    @Test
    public void maxTest(){
        Assert.assertEquals(recorder.max(), 100);
    }

    @Test
    public void summaryTest(){
        Assert.assertEquals(recorder.latencySummary().contains("p50"), true);
        Assert.assertEquals(recorder.latencySummary().contains("p90"), true);
        Assert.assertEquals(recorder.latencySummary().contains("p99.9"), true);
        Assert.assertEquals(recorder.latencySummary().contains("max"), true);
        Assert.assertEquals(recorder.latencySummary().contains("avg"), true);
    }
}
