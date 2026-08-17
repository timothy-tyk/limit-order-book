package validation;

import event.*;

import java.util.LinkedList;
import java.util.List;

public class EventRecorder implements EventListener {
    private List<Event> events;
    private boolean retainEvents;
    private long orderAcceptedCount;
    private long orderCancelledCount;
    private long orderModifiedCount;
    private long orderRejectedCount;
    private long tradeCount;
    private long marketOrderAcceptedCount;
    private long marketOrderRejectedCount;

    private long orderRejected_UnknownOrderCount;
    private long orderRejected_DuplicatedOrderIdCount;
    private long orderRejected_InvalidPriceCount;
    private long orderRejected_InvalidQtyCount;
    private long orderRejected_NoLiquidityCount;

    private long lastEventSequence = 0;

    public EventRecorder(boolean retainEvents) {
        this.events = retainEvents? new LinkedList<>():List.of();
        this.retainEvents = retainEvents;
    }

    @Override
    public void onEvent(Event event) {
        if(retainEvents){
            events.add(event);
        }
        lastEventSequence = event.eventSequence();

        switch(event){
            case OrderAccepted accepted -> orderAcceptedCount++;
            case OrderCancelled  cancelled -> orderCancelledCount++;
            case OrderModified modified -> orderModifiedCount++;
            case OrderRejected rejected -> handleOrderRejectedEvent(rejected);
            case Trade trade -> tradeCount++;
            case MarketOrderAccepted marketOrderAccepted -> marketOrderAcceptedCount++;
            case MarketOrderRejected marketOrderRejected -> marketOrderRejectedCount++;
        }
    }

    public void handleOrderRejectedEvent(OrderRejected rejected){
        orderRejectedCount++;
        switch(rejected.getReason()){
            case UNKNOWN_ORDER -> orderRejected_UnknownOrderCount++;
            case DUPLICATED_ORDER_ID -> orderRejected_DuplicatedOrderIdCount++;
            case INVALID_PRICE -> orderRejected_InvalidPriceCount++;
            case INVALID_QTY -> orderRejected_InvalidQtyCount++;
            case NO_LIQUIDITY -> orderRejected_NoLiquidityCount++;
        }
    }

    public List<Event> events(){
        if(!retainEvents){
            throw new UnsupportedOperationException(String.format("Retain Events = %s",retainEvents));
        }else{
            return List.copyOf(events);
        }
    }

    public long getOrderAcceptedCount() {
        return orderAcceptedCount;
    }

    public long getOrderCancelledCount() {
        return orderCancelledCount;
    }

    public long getOrderModifiedCount() {
        return orderModifiedCount;
    }

    public long getOrderRejectedCount() {
        return orderRejectedCount;
    }

    public long getTradeCount() {
        return tradeCount;
    }

    public long getMarketOrderAcceptedCount() {
        return marketOrderAcceptedCount;
    }

    public long getMarketOrderRejectedCount() {
        return marketOrderRejectedCount;
    }

    @Override
    public String summary(){
        StringBuffer sb = new StringBuffer();
        sb.append("Orders Accepted: "+getOrderAcceptedCount());
        sb.append("\n");
        sb.append("Orders Modified: "+getOrderModifiedCount());
        sb.append("\n");
        sb.append("Orders Cancelled: "+getOrderCancelledCount());
        sb.append("\n");
        sb.append("Orders Rejected: "+getOrderRejectedCount());
        sb.append("\n");
        sb.append("Rejection Reason: UNKNOWN_ORDER | "+orderRejected_UnknownOrderCount);
        sb.append("\n");
        sb.append("Rejection Reason: DUPLICATED_ORDER_ID | "+orderRejected_DuplicatedOrderIdCount);
        sb.append("\n");
        sb.append("Rejection Reason: INVALID_PRICE | "+orderRejected_InvalidPriceCount);
        sb.append("\n");
        sb.append("Rejection Reason: INVALID_QTY | "+orderRejected_InvalidQtyCount);
        sb.append("\n");
        sb.append("Rejection Reason: NO_LIQUIDITY | "+orderRejected_NoLiquidityCount);
        sb.append("\n");
        sb.append("Market Orders Accepted: "+getMarketOrderAcceptedCount());
        sb.append("\n");
        sb.append("Market Orders Rejected: "+getMarketOrderRejectedCount());
        sb.append("\n");
        sb.append("Total Trades: "+getTradeCount());
        sb.append("\n");
        sb.append("Last Event Sequence: "+lastEventSequence);
        return sb.toString();
    }
}
