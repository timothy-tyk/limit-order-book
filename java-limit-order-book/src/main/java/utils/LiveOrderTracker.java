package utils;

import event.*;
import event.EventListener;

import java.util.*;

public class LiveOrderTracker implements LiveOrderIdSource, EventListener {
    List<Long> liveOrderIds;
    Map<Long, Long> remainingQtyById;
    Map<Long, Integer> indexById;

    public LiveOrderTracker(){
        this.liveOrderIds = new ArrayList<>();
        this.remainingQtyById = new LinkedHashMap<>();
        this.indexById = new LinkedHashMap<>();
    }

    @Override
    public boolean hasLiveOrders() {
        return !liveOrderIds.isEmpty();
    }

    @Override
    public long randomLiveOrderId(Random random) {
        if(!hasLiveOrders()) throw new IllegalStateException("No new live orders available");
        else{
            int randomIndex = random.nextInt(liveOrderIds.size());
            return liveOrderIds.get(randomIndex);
        }
    }


    @Override
    public void onEvent(Event event) {
        switch (event){
            case OrderAccepted e -> add(e.getOrderId(), e.getQuantity());
            case Trade e -> handleTrade(e.getBuyOrderId(), e.getSellOrderId(), e.getQuantity());
            case OrderCancelled e -> remove(e.getOrderId());
            case OrderModified e -> handleUpdate(e);
            case MarketOrderAccepted e -> handleMarketOrderAccepted();
            case OrderRejected e -> handleOrderRejected();
            case MarketOrderRejected e -> handleOrderRejected();
        }
    }

    @Override
    public String summary() {
        return "";
    }

    private void add(long orderId, long qty){
        if(remainingQtyById.containsKey(orderId)){
            remainingQtyById.put(orderId,qty);
            return;
        }
        liveOrderIds.add(orderId);
        remainingQtyById.put(orderId,qty);
        indexById.put(orderId,liveOrderIds.size()-1);
    }

    private void handleTrade(long buyOrderId, long sellOrderId, long qty){
        reduce(buyOrderId, qty);
        reduce(sellOrderId, qty);
    }

    private void reduce(long orderId, long qty){
        if(remainingQtyById.containsKey(orderId)){
            long orderQty = remainingQtyById.get(orderId);

            long newOrderQty = orderQty-qty;
            if(newOrderQty<=0){
                remove(orderId);
            }else{
                remainingQtyById.put(orderId,newOrderQty);

            }
        }
    }

    private void remove(long orderId){
        if(remainingQtyById.containsKey(orderId)) {
            remainingQtyById.remove(orderId);
            int index = indexById.remove(orderId);

            //swap-with-last
            int lastIndex = liveOrderIds.size()-1;
            if (index < lastIndex) {
                long lastOrderId = liveOrderIds.get(lastIndex);
                liveOrderIds.set(index, lastOrderId);
                indexById.put(lastOrderId, index);
            }
            liveOrderIds.remove(lastIndex);
        }
    }

    private void handleUpdate(OrderModified e){
        remove(e.getOrderId());
        add(e.getOrderId(), e.getNewQuantity());
        //TODO: handle the new trade (if any)


    }

    private void handleMarketOrderAccepted(){
//        Market orders should not rest on the book, nothing to handle
    }

    private void handleOrderRejected(){
//        nothing to do
    }
}
