package benchmark;

import command.AddLimitOrderCommand;
import command.CancelOrderCommand;
import command.Command;
import command.ModifyOrderCommand;
import core.Side;
import engine.MatchingEngine;
import engine.concurrent.SynchronizedMatchingEngine;
import event.EventListener;
import utils.LiveOrderTracker;
import validation.EventRecorder;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ConcurrentBenchmarkRunner {
    public static void main() throws InterruptedException {
        List<WorkloadProfile> workloadProfiles = List.of(
                new WorkloadProfile("MT_ADD_ONLY", 42,1_000_000,100,0,0,0),
                new WorkloadProfile("MT_ADD_AND_MARKET", 42,1_000_000,100,0,0,15),
                new WorkloadProfile("MT_ADD_THEN_CANCEL", 42,1_000_000, 50,25,25,15),
                new WorkloadProfile("MT_THREAD_LOCAL_CHURN",42,1_000_000,80,10,10,15)
        );

        for(WorkloadProfile profile: workloadProfiles) {
            System.out.printf("=== %s | %s | %s ===",profile.getName(),profile.getSeed(),profile.getCommandCount());
            int[] threadCounts = {1,2,4,8};
            for (int i : threadCounts) {
                runMultithreaded(i, profile);
            }
        }


    }

    private static void runMultithreaded(int threadCount, WorkloadProfile profile) throws InterruptedException {
        long commandCount = 1_000_000;
        long commandsPerThread = commandCount/threadCount;

        EventListener eventRecorder = new EventRecorder(false);
        LatencyRecorder latencyRecorder = new LatencyRecorder(10000);
        LiveOrderTracker tracker = new LiveOrderTracker();
        SynchronizedMatchingEngine synchronizedMatchingEngine = new SynchronizedMatchingEngine(eventRecorder,latencyRecorder,tracker);

        CountDownLatch ready = new CountDownLatch(threadCount);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threadCount);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        for (int i = 0; i < threadCount; i++) {
            int threadId = i;
                executor.submit(() -> {
                    try {
                        //gets threads ready first
                        ready.countDown();
                        start.await();
                        runThread(threadId, synchronizedMatchingEngine, commandsPerThread,profile);
                        done.countDown();
                    }
                    catch (InterruptedException e) {
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
        executor.shutdown();
        double elapsedMillis = (endNanos-startNanos)/1_000_000.0;
        double throughput = 1_000_000/(elapsedMillis/1000);
        System.out.printf(
                "Threads: %d, Elapsed: %.3f ms, Throughput: %.2fM/sec%n",
                threadCount,
               elapsedMillis,
                throughput / 1_000_000.0
        );
        System.out.println(eventRecorder.summary());
        System.out.println(latencyRecorder.latencySummary());
        System.out.println(synchronizedMatchingEngine.getLiveOrderTracker().summary());
    }

//    private static void runMultithreaded(int threadCount) throws InterruptedException{
//        long commandCount = 1_000_000L;
//        long commandsPerThread = commandCount/threadCount;
//        EventRecorder eventRecorder= new EventRecorder(false);
//        LatencyRecorder latencyRecorder = new LatencyRecorder(1000);
//        LiveOrderTracker liveOrderTracker = new LiveOrderTracker();
//
//        SynchronizedMatchingEngine engine = new SynchronizedMatchingEngine(eventRecorder,latencyRecorder,liveOrderTracker);
//        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
//
//        CountDownLatch ready = new CountDownLatch(threadCount);
//        CountDownLatch start = new CountDownLatch(1);
//        CountDownLatch done = new CountDownLatch(threadCount);
//        for(int i=0; i<threadCount;i++){
//            final int threadId = i;
//            executorService.submit(()->{
//                try {
//                    ready.countDown();
//                    start.await();
//                    runThread(threadId, engine,commandsPerThread);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }finally{
//                    done.countDown();
//                }
//            });
//        }
//        ready.await();
//        long startNanos = System.nanoTime();
//        start.countDown();
//        done.await();
//        long endNanos = System.nanoTime();
//        executorService.shutdown();
//
//        double elapsedMilis = (endNanos-startNanos)/1_000_000;
//        double throughput = commandCount / ((endNanos - startNanos) / 1_000_000_000.0);
//        System.out.printf(
//                "Threads: %d, Elapsed: %.3f ms, Throughput: %.2fM/sec%n",
//                threadCount,
//               elapsedMilis,
//                throughput / 1_000_000.0
//        );
//        System.out.println(eventRecorder.summary());
//        System.out.println(latencyRecorder.latencySummary());
//        System.out.println(engine.getLiveOrderTracker().summary());
//    }

    private static void runThread(int threadId, SynchronizedMatchingEngine engine, long commandsPerThread, WorkloadProfile profile) {
        Random random = new Random(42 + threadId);
        long orderId = (1 + threadId) * 1_000_000L;
        long sequence = threadId * 1_000_000L;
        long basePrice = 100_000;

        for (long i = 0; i < commandsPerThread; i++) {
            Command command;
            int action = random.nextInt(100);
            if (action < profile.addPercent || !engine.getLiveOrderTracker().hasLiveOrders()) {
                Side side = random.nextBoolean() ? Side.BUY : Side.SELL;
                long priceOffset = random.nextInt(20) - 10;
                long qty = random.nextInt(100) + 1;
                AddLimitOrderCommand addLimitOrderCommand = new AddLimitOrderCommand(
                        sequence++,
                        orderId++,
                        side,
                        basePrice - priceOffset,
                        qty
                );
                command = addLimitOrderCommand;
                engine.submitCommand(command);
            } else if (action <= profile.addPercent + profile.cancelPercent) {
//                TOCTOU = Time of Check, Time of Use:
//                2 threads may pick the same orderIdToRemove at the same time, but only 1 thread can remove it,
//                the other thread will have its order rejected : Unknown_Order

//                Fix: Created synchronized method to pick random ID + submit command due to TOCTOU race condition
                engine.submitRandomCancel(sequence++,random);
            } else {
//                Created synchronized method to pick random ID + submit command due to TOCTOU race condition
                engine.submitRandomModify(sequence++,random, basePrice);
            }

        }
    }
}
