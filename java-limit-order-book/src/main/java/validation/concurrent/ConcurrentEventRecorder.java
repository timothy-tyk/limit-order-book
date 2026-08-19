package validation.concurrent;

import event.Event;
import event.EventListener;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class ConcurrentEventRecorder implements EventListener {
    private List<Event> events;
    private boolean retainEvents;
    private AtomicLong orderAcceptedCount;
    private AtomicLong orderCancelledCount;
    private AtomicLong orderModifiedCount;
    private AtomicLong orderRejectedCount;
    private AtomicLong tradeCount;
    private AtomicLong marketOrderAcceptedCount;
    private AtomicLong marketOrderRejectedCount;

    private AtomicLong orderRejected_UnknownOrderCount;
    private AtomicLong orderRejected_DuplicatedOrderIdCount;
    private AtomicLong orderRejected_InvalidPriceCount;
    private AtomicLong orderRejected_InvalidQtyCount;
    private AtomicLong orderRejected_NoLiquidityCount;

    private AtomicLong lastEventSequence = new AtomicLong(0);

    public ConcurrentEventRecorder(boolean retainEvents) {
        this.events = retainEvents? new LinkedList<>():List.of();
        this.retainEvents = retainEvents;
    }
    @Override
    public void onEvent(Event event) {

    }

    @Override
    public String summary() {
        return "";
    }
}
