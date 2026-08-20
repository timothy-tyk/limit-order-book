package concurrency;

import benchmark.LatencyRecorder;
import benchmark.WorkloadProfile;
import engine.concurrent.ReentrantLockMatchingEngine;
import engine.concurrent.SynchronizedMatchingEngine;
import event.EventListener;
import utils.LiveOrderTracker;
import validation.EventRecorder;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConcurrentWorkloadRunner {

    public void runConcurrentWorkload(int threadCount, WorkloadProfile profile) throws InterruptedException {
        final boolean RETAIN_EVENTS=false;
        EventListener eventListener = new EventRecorder(RETAIN_EVENTS);
        LatencyRecorder latencyRecorder = new LatencyRecorder(10000);
        LiveOrderTracker liveOrderTracker = new LiveOrderTracker();
        SynchronizedMatchingEngine synchronizedMatchingEngine = new SynchronizedMatchingEngine(eventListener, latencyRecorder, liveOrderTracker);
        ReentrantLockMatchingEngine reentrantLockMatchingEngine = new ReentrantLockMatchingEngine(eventListener, latencyRecorder, liveOrderTracker);

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch ready = new CountDownLatch(threadCount);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threadCount);

        WorkerCommandGenerator workerCommandGenerator = new WorkerCommandGenerator(profile, synchronizedMatchingEngine);

        for(int i=0;i<threadCount;i++){
            int threadId = i;
            executorService.submit(()->{
                try {
                    //gets threads ready first
                    ready.countDown();
                    //dont start yet until all threads are ready and timer is ticking
                    start.await();
                    workerCommandGenerator.generate(threadId, threadCount, false);
                    done.countDown();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        //wait until all threads are ready
        ready.await();
        long startNanos = System.nanoTime();
        //let all blocked threads at start.await() run
        start.countDown();
        //blocks threads until all done.countDown() is completed
        done.await();
        long endNanos = System.nanoTime();
        executorService.shutdown();
        double elapsedMillis = (endNanos-startNanos)/1_000_000.0;
        double throughput = 1_000_000/(elapsedMillis/1000);
        System.out.printf(
                "Threads: %d, Elapsed: %.3f ms, Throughput: %.2fM/sec%n",
                threadCount,
                elapsedMillis,
                throughput / 1_000_000.0
        );
        System.out.println(eventListener.summary());
        System.out.println(latencyRecorder.latencySummary());
        System.out.println(synchronizedMatchingEngine.getLiveOrderTracker().summary());
    }
}
