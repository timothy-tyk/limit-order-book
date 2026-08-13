package workload;

import command.AddLimitOrderCommand;
import command.CancelOrderCommand;
import command.Command;
import command.ModifyOrderCommand;
import core.Side;
import engine.MatchingEngine;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

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
        Deque<Long> liveOrderIds = new ArrayDeque<>();
        long nextOrderId = 1;
        long sequence = 1;

        long basePrice = 100_000;

        for(long i=0;i<commandCount;i++){
            Command command;

            int action = random.nextInt(100);

            if(action<70 || liveOrderIds.isEmpty()){
                System.out.println("AddLimitOrderCommand - "+sequence+" "+nextOrderId);
//              Add Limit Order (70%)
                Side side = random.nextBoolean()?Side.BUY:Side.SELL;
                long priceOffset = random.nextInt(20)-10;
                long price = basePrice+priceOffset;
                long qty = random.nextInt(100)+1;
                command = new AddLimitOrderCommand(
                        sequence,
                        nextOrderId,
                        side,
                        price,
                        qty
                );
                liveOrderIds.addLast(nextOrderId);
                nextOrderId++;
            }else if(action<90){
                System.out.println("CancelOrderCommand - "+sequence+" "+nextOrderId);
//              Cancel Limit Order (20%)
                long orderIdToRemove = removeLiveOrderId(random, liveOrderIds);
                command = new CancelOrderCommand(sequence, orderIdToRemove);
            }else{
                System.out.println("ModifyOrderCommand - "+sequence+" "+nextOrderId);
//              Modify Limit Order (10%)
                long orderIdToRemove = removeLiveOrderId(random, liveOrderIds);
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
        return liveOrderIds.size();
    }

    private long removeLiveOrderId(Random random, Deque<Long> liveOrderIds){
        long index = random.nextInt(liveOrderIds.size()-1);
        long current = 0;
        for(Long orderId:liveOrderIds){
            if(current == index){
                liveOrderIds.remove(orderId);
                return orderId;
            }
            current++;
        }
        return liveOrderIds.removeFirst();
    }
}
