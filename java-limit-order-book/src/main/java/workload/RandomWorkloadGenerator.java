package workload;

import command.*;
import core.Side;
import engine.MatchingEngine;

import java.util.*;

public final class RandomWorkloadGenerator implements WorkloadGenerator{
    private final long seed;
    private final MatchingEngine engine;

    public RandomWorkloadGenerator(long seed, MatchingEngine engine) {
        this.seed = seed;
        this.engine = engine;
    }

    @Override
    public long generate(long commandCount){
        Random random = new Random(seed);
        List<Long> liveOrderIds = new ArrayList<>();
        long nextOrderId = 1;
        long sequence = 1;

        long basePrice = 100_000;

        for(long i=0;i<commandCount;i++){
            Command command;

            int action = random.nextInt(100);

            if(action<70 || liveOrderIds.isEmpty()){
//                System.out.println("AddLimitOrderCommand - "+sequence+" "+nextOrderId);
//              Add Limit Order (70%)
                Side side = random.nextBoolean()?Side.BUY:Side.SELL;
                long priceOffset = random.nextInt(20)-10;
                long price = basePrice+priceOffset;
                long qty = random.nextInt(100)+1;
                int marketOrNot = random.nextInt(100);
                if(marketOrNot<10){ //% of orders are Market
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
                liveOrderIds.addLast(nextOrderId);
                nextOrderId++;
            }else if(action<90){
//                System.out.println("CancelOrderCommand - "+sequence+" "+nextOrderId);
//              Cancel Limit Order (20%)
                long orderIdToRemove = removeRandomLiveOrderId(random, liveOrderIds);
                command = new CancelOrderCommand(sequence, orderIdToRemove);
            }else{
//                System.out.println("ModifyOrderCommand - "+sequence+" "+nextOrderId);
//              Modify Limit Order (10%)
                long orderIdToRemove = removeRandomLiveOrderId(random, liveOrderIds);
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
        engine.showEventSummary();
        return liveOrderIds.size();
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

//        SWAP WITH LAST PATTERN (200ms!!):
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
