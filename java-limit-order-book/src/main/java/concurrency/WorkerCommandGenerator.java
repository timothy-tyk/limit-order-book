package concurrency;

import benchmark.WorkloadProfile;
import command.AddLimitOrderCommand;
import command.Command;
import core.Side;
import engine.MatchingEngine;
import engine.concurrent.ConcurrentMatchingEngine;
import utils.LiveOrderTracker;
import workload.ConcurrentWorkloadGenerator;
import workload.WorkloadGenerator;

import java.util.Random;

public class WorkerCommandGenerator implements ConcurrentWorkloadGenerator {
    private final WorkloadProfile profile;
    private final ConcurrentMatchingEngine engine;
    private LiveOrderTracker liveOrderTracker;

    public WorkerCommandGenerator(WorkloadProfile profile, ConcurrentMatchingEngine engine) {
        this.profile = profile;
        this.engine = engine;
        this.liveOrderTracker = engine.getLiveOrderTracker();
    }

    @Override
    public void generate(int threadId,int threadCount,boolean measuredRun) {
        Random random = new Random(profile.getSeed()+threadId);
        long commandsPerThread = profile.getCommandCount()/threadCount;

        long nextOrderId = (1 + threadId) * 1_000_000L;
        long sequence =  threadId*1_000_000L;
        long basePrice = 100_000;

        for(int i=0;i<commandsPerThread;i++){
            int action = random.nextInt(100);
            Command command;
            if (action < profile.getAddPercent() || !liveOrderTracker.hasLiveOrders()) {
                Side side = random.nextBoolean() ? Side.BUY : Side.SELL;
                long priceOffset = random.nextInt(20) - 10;
                long qty = random.nextInt(100) + 1;
                AddLimitOrderCommand addLimitOrderCommand = new AddLimitOrderCommand(
                        sequence++,
                        nextOrderId++,
                        side,
                        basePrice - priceOffset,
                        qty
                );
                command = addLimitOrderCommand;
                engine.submitCommand(command);
            } else if (action <= profile.getAddPercent() + profile.getCancelPercent()) {
//              TOCTOU = Time of Check, Time of Use:
//              2 threads may pick the same orderIdToRemove at the same time, but only 1 thread can remove it,
//              the other thread will have its order rejected : Unknown_Order
//
//              Fix: Created synchronized method to pick random ID + submit command due to TOCTOU race condition
                engine.submitRandomCancel(sequence++, random);
            } else {
//              Created synchronized method to pick random ID + submit command due to TOCTOU race condition
                engine.submitRandomModify(sequence++, random, basePrice);
            }
    }
        if(measuredRun) {
            System.out.println("================");
            engine.showProfileSummary(profile);
            engine.showLatencySummary();
            engine.showEventSummary();
        }

    }

    @Override
    public long getLastProcessedSequence() {
        return engine.lastProcessedSequence();
    }


}
