package benchmark;

import command.AddLimitOrderCommand;
import command.CancelOrderCommand;
import command.Command;
import command.ModifyOrderCommand;
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
//                new WorkloadProfile("MT_ADD_ONLY", 42, 1_000_000, 100, 0, 0, 0),
//                new WorkloadProfile("MT_ADD_AND_MARKET", 42, 1_000_000, 100, 0, 0, 15),
                new WorkloadProfile("MT_ADD_THEN_CANCEL", 42, 1_000_000, 50, 25, 25, 15)
//                new WorkloadProfile("MT_THREAD_LOCAL_CHURN", 42, 1_000_000, 80, 10, 10, 15)
        );

        int[] threads = {1,2,4,8};
        for(WorkloadProfile profile: workloadProfiles){
            System.out.printf("%s | %s | %s \n",profile.getName(), profile.getCommandCount(), profile.getSeed());
            for(int threadCount: threads){
                runMultithreaded(threadCount, profile);
            }
        }
    }

    private static void runMultithreaded(int threadCount, WorkloadProfile profile) throws InterruptedException {
        long commandCount = profile.getCommandCount();
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
                long price;
                if (side == Side.BUY) {
                    price = basePrice - (random.nextInt(50) + 1);   // 99,950 to 99,999
                } else {
                    price = basePrice + (random.nextInt(50) + 1);   // 100,001 to 100,050
                }
//                long priceOffset = random.nextInt(20) - 10;
                long qty = random.nextInt(100) + 1;
                AddLimitOrderCommand addLimitOrderCommand = new AddLimitOrderCommand(
                        sequence++,
                        orderId++,
                        side,
                        price,
                        qty
                );
                command = addLimitOrderCommand;
                engine.submitCommand(command);
            } else if (action <= profile.addPercent + profile.cancelPercent) {
                long orderIdToCancel = engine.getLiveOrderTracker().randomLiveOrderId(random);
                CancelOrderCommand cancelOrderCommand = new CancelOrderCommand(sequence++,orderIdToCancel);
                command = cancelOrderCommand;
                engine.submitCommand(command);

//                TODO: Should producer or consumer thread decide which order to remove?

            } else {
                long orderIdToModify = engine.getLiveOrderTracker().randomLiveOrderId(random);
                Side newSide = random.nextBoolean() ? Side.BUY : Side.SELL;
                long priceOffset = random.nextInt(20) - 10;
                long newPrice = basePrice - priceOffset;
                long newQty = random.nextInt(100) + 1;
                ModifyOrderCommand modifyOrderCommand = new ModifyOrderCommand(sequence++,orderIdToModify, newSide, newPrice, newQty);
                engine.submitCommand(modifyOrderCommand);
            }

        }
    }


}
