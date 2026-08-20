package concurrent;

import benchmark.LatencyRecorder;
import benchmark.WorkloadProfile;
import concurrency.WorkerCommandGenerator;
import engine.concurrent.ReentrantLockMatchingEngine;
import engine.concurrent.SynchronizedMatchingEngine;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import utils.LiveOrderTracker;
import validation.EventRecorder;

public class WorkerCommandGeneratorTest {
    WorkloadProfile testProfile;
    SynchronizedMatchingEngine synchronizedMatchingEngine;
    WorkerCommandGenerator workerCommandGeneratorSync;

    ReentrantLockMatchingEngine reentrantLockMatchingEngine;
    WorkerCommandGenerator workerCommandGeneratorReentrant;

    @Before
    public void setup(){
        testProfile = new WorkloadProfile("Test",1,100_000,50,25,25,15);
        synchronizedMatchingEngine = new SynchronizedMatchingEngine(new EventRecorder(false),new LatencyRecorder(1000),new LiveOrderTracker());
        workerCommandGeneratorSync = new WorkerCommandGenerator(testProfile,synchronizedMatchingEngine);

        reentrantLockMatchingEngine = new ReentrantLockMatchingEngine(new EventRecorder(false),new LatencyRecorder(1000),new LiveOrderTracker());
        workerCommandGeneratorReentrant = new WorkerCommandGenerator(testProfile, reentrantLockMatchingEngine);
    }

    @Test
    public void multithreadedAddOnlyStressTestWithSynchronizedMatchingEngine(){
        workerCommandGeneratorSync.generate(0,5,false);
        synchronizedMatchingEngine.showEventSummary();
        synchronizedMatchingEngine.showLatencySummary();
        synchronizedMatchingEngine.showProfileSummary(testProfile);
        Assert.assertEquals(workerCommandGeneratorSync.getLastProcessedSequence(),19_999);
    }


    @Test
    public void multithreadedAddOnlyStressTestWithReentrantLockMatchingEngine(){
        workerCommandGeneratorReentrant.generate(0,5,false);
        reentrantLockMatchingEngine.showEventSummary();
        reentrantLockMatchingEngine.showLatencySummary();
        reentrantLockMatchingEngine.showProfileSummary(testProfile);
        Assert.assertEquals(workerCommandGeneratorReentrant.getLastProcessedSequence(),19_999);
    }
}

