package utils;

import java.util.Random;

public interface LiveOrderIdSource {
    boolean hasLiveOrders();
    long randomLiveOrderId(Random random);
}
