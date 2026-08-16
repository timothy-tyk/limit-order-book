package validation;

import core.OrderBook;
import engine.MatchingEngine;

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
        System.out.println("Invariance Check Complete");
    }

}
