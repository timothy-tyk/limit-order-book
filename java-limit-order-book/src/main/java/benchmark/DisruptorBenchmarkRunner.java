package benchmark;

import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;
import command.*;
import core.Side;
import engine.disruptor.DisruptorMatchingEngine;
import engine.singlewriter.SingleWriterMatchingEngine;
import event.EventListener;
import utils.Constants;
import utils.LiveOrderTracker;
import validation.EventRecorder;
import validation.InvariantChecker;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Milestone 6 : Disruptor Ring Buffer Matching Engine
 */
public class DisruptorBenchmarkRunner {
    static void main() throws InterruptedException {
        List<WorkloadProfile> workloadProfiles = List.of(
                new WorkloadProfile("MT_ADD_ONLY", 42, 1_000_000, 100, 0, 0, 0),
                new WorkloadProfile("MT_ADD_THEN_CANCEL", 42, 1_000_000, 50, 50, 0, 0)
            );
        int[] threads = {1,2,4,8};
        for(WorkloadProfile profile: workloadProfiles){
            System.out.printf("=== Profile: %s | Commands: %s | Seed: %s ===\n",profile.getName(), profile.getCommandCount(), profile.getSeed());
            for(int threadCount: threads){
                runMultithreaded(threadCount, profile);
            }
        }
    }

    private static void runMultithreaded(int threadCount, WorkloadProfile profile) throws InterruptedException{
        long commandCount = profile.getCommandCount();
        long commandsPerThread = commandCount/threadCount;

        EventListener eventRecorder = new EventRecorder(Constants.RETAIN_EVENTS);
        LatencyRecorder latencyRecorder = new LatencyRecorder(10000);
        LiveOrderTracker tracker = new LiveOrderTracker();
        WaitStrategy waitStrategy = new YieldingWaitStrategy();
        DisruptorMatchingEngine disruptorMatchingEngine = new DisruptorMatchingEngine(eventRecorder,latencyRecorder,tracker, Constants.QUEUE_CAPACITY, waitStrategy);

        CountDownLatch ready = new CountDownLatch(threadCount);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threadCount);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        disruptorMatchingEngine.start();
        for (int i = 0; i < threadCount; i++) {
            int threadId = i;
            executor.submit(() -> {
                try {
                    //gets threads ready first
                    ready.countDown();
                    start.await();
                    if(profile.getName().equals("MT_ADD_THEN_CANCEL")){
                        runThreadForAddThenCancel(threadId, threadCount, disruptorMatchingEngine, commandsPerThread,profile);
                    }else {
                        runThread(threadId, disruptorMatchingEngine, commandsPerThread, profile);
                    }
                }
                catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }finally{
//                  countdown in finally-block to prevent stalling when runThread(...) throws error
                    done.countDown();
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
        disruptorMatchingEngine.awaitQueueCompletion(10_000);
        long endNanos = System.nanoTime();
        executor.shutdown();
        disruptorMatchingEngine.stop();
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
        System.out.println(disruptorMatchingEngine.getLiveOrderTracker().summary());
        System.out.println("Submitted Commands: "+ disruptorMatchingEngine.getSubmittedCommands());
        System.out.println("Processed Commands: "+ disruptorMatchingEngine.getProcessedCommands());
        InvariantChecker.check(disruptorMatchingEngine);
    }

    private static void runThread(int threadId, DisruptorMatchingEngine engine, long commandsPerThread, WorkloadProfile profile) {
        Random random = new Random(42 + threadId);
        long orderId = (1 + threadId) * 1_000_000L;
        long sequence = threadId * 1_000_000L;
        long basePrice = 100_000;
        long submittedCount=0;

        while(submittedCount<commandsPerThread) {
            Command command;
            int action = random.nextInt(100);
            if (action < profile.addPercent || !engine.getLiveOrderTracker().hasLiveOrders()) {
                if(action<profile.marketPercent){
                    MarketOrderCommand marketOrderCommand = new MarketOrderCommand(
                            sequence++,
                            orderId++,
                            random.nextBoolean() ? Side.BUY : Side.SELL,
                            random.nextInt(100) + 1
                    );
                    command = marketOrderCommand;
                    engine.submitCommand(command);
                    submittedCount++;
                }else {
                    Side side = random.nextBoolean() ? Side.BUY : Side.SELL;
                    long priceOffset = random.nextInt(20) - 10;
                    long price = basePrice - priceOffset;
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
                    submittedCount++;
                }
            } else if (action <= profile.addPercent + profile.cancelPercent) {
                if(!engine.getLiveOrderTracker().hasLiveOrders()) continue; //guard cancel against no-live-order scenario

                long orderIdToCancel;
                try{
                    // Catch the race condition where tracker becomes empty
                    // between the check above (if(!engine.....)) and this method call
                    orderIdToCancel = engine.getLiveOrderTracker().randomLiveOrderId(random);
                }catch (IllegalStateException e){
                    continue; // retry the iteration
                }
                CancelOrderCommand cancelOrderCommand = new CancelOrderCommand(sequence++,orderIdToCancel);
                command = cancelOrderCommand;
                engine.submitCommand(command);
                submittedCount++;

            } else {
                if(!engine.getLiveOrderTracker().hasLiveOrders()) continue; //guard modify against no-live-order scenario

                long orderIdToModify;
                try{
                    // Catch the race condition where tracker becomes empty
                    // between the check above and this method call
                    orderIdToModify = engine.getLiveOrderTracker().randomLiveOrderId(random);
                }catch (IllegalStateException e){
                    continue; // retry the iteration
                }
                Side newSide = random.nextBoolean() ? Side.BUY : Side.SELL;
                long priceOffset = random.nextInt(20) - 10;
                long newPrice = basePrice - priceOffset;
                long newQty = random.nextInt(100) + 1;
                ModifyOrderCommand modifyOrderCommand = new ModifyOrderCommand(sequence++,orderIdToModify, newSide, newPrice, newQty);
                engine.submitCommand(modifyOrderCommand);
                submittedCount++;
            }

        }
    }

    private static void runThreadForAddThenCancel(int threadId, int threadCount, DisruptorMatchingEngine engine, long commandsPerThread, WorkloadProfile profile) {
        Random random = new Random(42 + threadId);
        long orderId = (1 + threadId) * 1_000_000L;
        long sequence = threadId * 1_000_000L;
        long basePrice = 100_000;
        long submittedCount = 0;
        long orderIdToCancel = (1 + threadId) * 1_000_000L;
        Command command;
        while (submittedCount < commandsPerThread) {
            if(submittedCount<commandsPerThread/2){
                // Add + BUY for first half of orders
                long priceOffset = random.nextInt(20) - 10;
                long price = basePrice - priceOffset;
                long qty = random.nextInt(100) + 1;
                AddLimitOrderCommand addLimitOrderCommand = new AddLimitOrderCommand(
                        sequence++,
                        orderId++,
                        Side.BUY,
                        price,
                        qty
                );
                command = addLimitOrderCommand;
                engine.submitCommand(command);
                submittedCount++;
            }else{
                // Cancel orders for 2nd half
                CancelOrderCommand cancelOrderCommand = new CancelOrderCommand(sequence++, orderIdToCancel);
                orderIdToCancel++;
                command = cancelOrderCommand;
                engine.submitCommand(command);
                submittedCount++;
            }
        }
    }
}
