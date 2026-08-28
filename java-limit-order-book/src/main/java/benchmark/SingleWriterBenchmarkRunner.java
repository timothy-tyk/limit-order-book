package benchmark;

import command.AddLimitOrderCommand;
import command.Command;
import core.Side;
import engine.MatchingEngine;
import engine.concurrent.SynchronizedMatchingEngine;
import engine.singlewriter.SingleWriterMatchingEngine;
import event.EventListener;
import utils.LiveOrderTracker;
import validation.EventRecorder;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SingleWriterBenchmarkRunner {
    public static void main() throws InterruptedException {
        List<WorkloadProfile> workloadProfiles = List.of(
                new WorkloadProfile("MT_ADD_ONLY", 42, 1_000_000, 100, 0, 0, 0),
                new WorkloadProfile("MT_ADD_AND_MARKET", 42, 1_000_000, 100, 0, 0, 15)
        );

        int[] threads = {1,2,4,8};
        for(WorkloadProfile profile: workloadProfiles){
            for(int threadCount: threads){
                runMultithreaded(threadCount, profile);
            }
        }
    }

    private static void runMultithreaded(int threadCount, WorkloadProfile profile) throws InterruptedException {
        long commandCount = 1_000_000;
        long commandsPerThread = commandCount/threadCount;

        EventListener eventRecorder = new EventRecorder(false);
        LatencyRecorder latencyRecorder = new LatencyRecorder(10000);
        LiveOrderTracker tracker = new LiveOrderTracker();
//        SynchronizedMatchingEngine synchronizedMatchingEngine = new SynchronizedMatchingEngine(eventRecorder,latencyRecorder,tracker);
        SingleWriterMatchingEngine singleWriterMatchingEngine = new SingleWriterMatchingEngine(eventRecorder,latencyRecorder,tracker,65_316);

        CountDownLatch ready = new CountDownLatch(threadCount);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threadCount);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        singleWriterMatchingEngine.start();
        for (int i = 0; i < threadCount; i++) {
            int threadId = i;
            executor.submit(() -> {
                try {
                    //gets threads ready first
                    ready.countDown();
                    start.await();
//                    runThread(threadId, synchronizedMatchingEngine, commandsPerThread,profile);
                    runThread(threadId, singleWriterMatchingEngine, commandsPerThread, profile);

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
        singleWriterMatchingEngine.awaitQueueCompletion(10_000);
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
        System.out.println(singleWriterMatchingEngine.getLiveOrderTracker().summary());
        System.out.println("Submitted Commands: "+ singleWriterMatchingEngine.getSubmittedCommands());
        System.out.println("Processed Commands: "+ singleWriterMatchingEngine.getProcessedCommands());
    }

    private static void runThread(int threadId, SingleWriterMatchingEngine engine, long commandsPerThread, WorkloadProfile profile) {
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
//                engine.submitRandomCancel(sequence++,random);
            } else {
//                Created synchronized method to pick random ID + submit command due to TOCTOU race condition
//                engine.submitRandomModify(sequence++,random, basePrice);
            }

        }
    }
}
