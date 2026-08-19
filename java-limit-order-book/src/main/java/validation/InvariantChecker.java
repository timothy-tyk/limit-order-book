package validation;

import core.Order;
import core.OrderBook;
import core.PriceLevel;
import engine.MatchingEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

public class InvariantChecker {
    public static void check(MatchingEngine engine){
        /**
         * Check for the following:
         *  Bids and asks are not crossed.
         *  No price level has zero or negative quantity.
         *  Order IDs are unique.
         *  Total resting quantity equals sum of order quantities.
         *  Best bid price < best ask price.
         *  All orders in the ID map exist in the book.
         *  All orders in the book exist in the ID map.
         */
        OrderBook orderBook = engine.getOrderBook();
        List<Order> fullBidOrderList = new ArrayList<>();
        List<Order> fullAskOrderList = new ArrayList<>();

//          No price level has zero or negative quantity.
        boolean priceLevelQtyCheck = true;
        System.out.println("Invariance Check Started");
        for(Long key: orderBook.getBids().keySet()){
            if(orderBook.getBids().get(key).getTotalQuantity()<=0){
                System.out.println("Bid PriceLevel Qty Check: "+orderBook.getBids().get(key).getTotalQuantity()+"@"+key);
            }
        }
        for(Long key: orderBook.getAsks().keySet()){
            if(orderBook.getAsks().get(key).getTotalQuantity()<=0){
                System.out.println("Asks PriceLevel Qty Check: "+orderBook.getAsks().get(key).getTotalQuantity()+"@"+key);
            }
        }

//        Total resting quantity equals sum of order quantities.
        for(Long key: orderBook.getBids().keySet()){
            PriceLevel pl = orderBook.getBids().get(key);
            long qty = 0;
            for(Order o: pl.getOrders().values()){
                fullBidOrderList.add(o);
                qty+=o.getRemainingQuantity();
            }
            if(qty != pl.getTotalQuantity()) System.out.println("Error in Bid PriceLevel TotalQty Check: "+ key);
        }

        for(Long key: orderBook.getAsks().keySet()){
            PriceLevel pl = orderBook.getAsks().get(key);
            long qty = 0;
            for(Order o: pl.getOrders().values()){
                fullAskOrderList.add(o);
                qty+=o.getRemainingQuantity();
            }
            if(qty != pl.getTotalQuantity()) System.out.println("Error in Bid PriceLevel TotalQty Check: "+ key);
        }

//        Best bid price < best ask price.
        if(!orderBook.getBids().isEmpty() && !orderBook.getAsks().isEmpty()){
            if(orderBook.getBids().firstKey()>=orderBook.getAsks().firstKey()) {
                System.out.println("Best bid price > best ask price");
            }
        }

//        All orders in the ID map exist in the book.
        if(fullBidOrderList.size()+ fullAskOrderList.size()!=orderBook.getOrdersById().size()){
            System.out.println("OrderList sizes do not tally");
        }
        for(Order bidOrder: fullBidOrderList){
            orderBook.getOrdersById().remove(bidOrder.getOrderId());
        }
        for(Order askOrder: fullAskOrderList){
            orderBook.getOrdersById().remove(askOrder.getOrderId());
        }
        if(orderBook.getOrdersById().size()>0){
            System.out.println("Orderbook order list contains more orders than the PriceLevel lists");
        }

        System.out.println("Invariance Check Complete");
    }

}
