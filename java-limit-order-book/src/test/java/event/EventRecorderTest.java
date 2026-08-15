package event;

import core.Side;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import validation.EventRecorder;

import java.util.ArrayList;
import java.util.List;

public class EventRecorderTest {
    private List<Event> eventList;
    private EventRecorder listener;
    private final boolean RETAIN_EVENTS = true;

    @Before
    public void setup(){
        eventList = new ArrayList<>();
        listener = new EventRecorder(RETAIN_EVENTS);
    }

    @Test
    public void eventRecorderListTest(){
        eventList.add(new OrderAccepted(1,1,1, Side.BUY,100L,100));
        eventList.add(new OrderCancelled(2,2,1));
        eventList.add(new OrderAccepted(3,3,2, Side.BUY,100L,100));
        eventList.add(new OrderModified(4,4,2,Side.SELL,150L,50));
        eventList.add(new OrderAccepted(5,5,3, Side.BUY,200L,100));
        eventList.add(new Trade(6,6,3,2,150L,50));
        for(Event e: eventList){
            listener.onEvent(e);
        }
        Assert.assertEquals(listener.events().size(),6);
        Assert.assertEquals(listener.getOrderAcceptedCount(),3);
        Assert.assertEquals(listener.getOrderCancelledCount(),1);
        Assert.assertEquals(listener.getOrderModifiedCount(),1);
        Assert.assertEquals(listener.getTradeCount(),1);
    }

    @Test
    public void eventRecorderSummaryTest(){
        eventList.add(new OrderAccepted(1,1,1, Side.BUY,100L,100));
        eventList.add(new OrderCancelled(2,2,1));
        eventList.add(new OrderAccepted(3,3,2, Side.BUY,100L,100));
        eventList.add(new OrderModified(4,4,2,Side.SELL,150L,50));
        eventList.add(new OrderAccepted(5,5,3, Side.BUY,200L,100));
        eventList.add(new Trade(6,6,3,2,150L,50));
        for(Event e: eventList){
            listener.onEvent(e);
        }
        Assert.assertEquals(listener.summary().contains("Orders Accepted"), true);
        Assert.assertEquals(listener.summary().contains("Orders Rejected"), true);
        Assert.assertEquals(listener.summary().contains("Orders Cancelled"), true);
        Assert.assertEquals(listener.summary().contains("Orders Modified"), true);
        Assert.assertEquals(listener.summary().contains("Market Orders Accepted"), true);
        Assert.assertEquals(listener.summary().contains("Market Orders Rejected"), true);
        Assert.assertEquals(listener.summary().contains("Total Trades"), true);
        Assert.assertEquals(listener.summary().contains("Last Event Sequence"), true);
    }
}
