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
            case OrderRejected rejected -> orderRejectedCount++;
            case Trade trade -> tradeCount++;
            case MarketOrderAccepted marketOrderAccepted -> marketOrderAcceptedCount++;
            case MarketOrderRejected marketOrderRejected -> marketOrderRejectedCount++;
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

    public String summary(){
        StringBuffer sb = new StringBuffer();
        sb.append("Orders Accepted: "+orderAcceptedCount);
        sb.append("\n");
        sb.append("Orders Modified: "+orderModifiedCount);
        sb.append("\n");
        sb.append("Orders Cancelled: "+orderCancelledCount);
        sb.append("\n");
        sb.append("Orders Rejected: "+orderRejectedCount);
        sb.append("\n");
        sb.append("Market Orders Accepted: "+marketOrderAcceptedCount);
        sb.append("\n");
        sb.append("Market Orders Rejected: "+marketOrderRejectedCount);
        sb.append("\n");
        sb.append("Total Trades: "+tradeCount);
        sb.append("\n");
        sb.append("Last Event Sequence: "+lastEventSequence);
        return sb.toString();
    }
}
