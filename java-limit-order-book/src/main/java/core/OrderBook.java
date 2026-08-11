package core;

import java.util.Map;
import java.util.TreeMap;

public class OrderBook {
    private TreeMap<Long, PriceLevel> bids;
    private TreeMap<Long, PriceLevel> asks;
    private Map<Long, Order> ordersById;
}
