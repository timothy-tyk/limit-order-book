package workload;

import benchmark.WorkloadProfile;
import command.*;
import core.Side;
import engine.MatchingEngine;
import utils.LiveOrderTracker;

import java.util.*;

public final class RandomWorkloadGenerator implements WorkloadGenerator{
    private final WorkloadProfile profile;
    private final MatchingEngine engine;
    private LiveOrderTracker tracker;

    public RandomWorkloadGenerator(WorkloadProfile profile, MatchingEngine engine) {
        this.profile = profile;
        this.engine = engine;
        this.tracker = engine.getLiveOrderTracker();
    }

    @Override
    public void generate(boolean measuredRun){
        Random random = new Random(profile.getSeed());
        long commandCount = profile.getCommandCount();

        long nextOrderId = 1;
        long sequence = 1;

        long basePrice = 100_000;

        for(long i=0;i<commandCount;i++){
            Command command;

            int action = random.nextInt(100);

            if(action< profile.getAddPercent() || !tracker.hasLiveOrders()){
//              Add Limit Order (70%)
                Side side = random.nextBoolean()?Side.BUY:Side.SELL;
                long priceOffset = random.nextInt(20)-10;
                long price = basePrice+priceOffset;
                long qty = random.nextInt(100)+1;
                int marketOrNot = random.nextInt(100);
                if(marketOrNot<profile.getMarketPercent()){ //% of orders are Market
                    command = new MarketOrderCommand(sequence, nextOrderId, side, qty);
                }else {
                    command = new AddLimitOrderCommand(
                            sequence,
                            nextOrderId,
                            side,
                            price,
                            qty
                    );
                }
                nextOrderId++;
            }else if(action<100-profile.getCancelPercent()){
//              Cancel Limit Order (20%)
                long orderIdToRemove = tracker.randomLiveOrderId(random);
                command = new CancelOrderCommand(sequence, orderIdToRemove);
            }else{
//              Modify Limit Order (10%)
                long orderIdToRemove = tracker.randomLiveOrderId(random);
                Side newSide = random.nextBoolean()?Side.BUY:Side.SELL;
                long priceOffset = random.nextInt(20)-10;
                long newPrice = basePrice+priceOffset;
                long newQty = random.nextInt(100)+1;

                command = new ModifyOrderCommand(sequence, orderIdToRemove,newSide,newPrice,newQty);
            }
//          Submit command
            engine.submitCommand(command);
            sequence++;
        }
        if(measuredRun) {
            System.out.println("================");
            engine.showProfileSummary(profile);
            engine.showLatencySummary();
            engine.showEventSummary();
        }

    }

    private long removeRandomLiveOrderId(Random random, List<Long> liveOrderIds){
//        WRONG: long index = random.nextInt(liveOrderIds.size());
//        using long type will force Java to use List.remove(Object)
//        time = O(n) instead of O(1).
//        Normal remove method below: (~2000ms)
//        int index = random.nextInt(liveOrderIds.size());
//        try{
//            return liveOrderIds.get(index);
//
//        }finally{
//            liveOrderIds.remove(index); //O(n) - shifts
//        }

//        SWAP-WITH-LAST PATTERN (200ms!!):
        int index = random.nextInt(liveOrderIds.size());
        int lastIndex = liveOrderIds.size()-1;
        long orderId = liveOrderIds.get(index);
        if(index!=lastIndex){
            liveOrderIds.set(index, liveOrderIds.get(lastIndex)); //O(1)
        }
        liveOrderIds.remove(lastIndex); // O(1) – removes the tail, no shift
        return orderId;
    }

    @Override
    public long getLastProcessedSequence(){
        return engine.lastProcessedSequence();
    }
}
