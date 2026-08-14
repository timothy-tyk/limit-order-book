package event;

public sealed interface Event permits
        OrderAccepted,
        OrderModified,
        OrderRejected,
        OrderCancelled,
        MarketOrderAccepted,
        MarketOrderRejected,
        Trade {

    long eventSequence();
    long commandSequence();
}

/**
 * For AddLimitOrderCommand
 * If order is valid:
 *     emit OrderAccepted
 *
 * Then try matching.
 * If matches occur:
 *     emit Trade events.
 * If quantity remains:
 *     rest remainder on book.
 */

/**
 * For CancelOrderCommand
 * If order exists:
 *     remove order
 *     emit OrderCancelled
 * Else:
 *     emit OrderRejected
 */

/**
 * For ModifyOrderCommand
 * If order exists and modification is valid:
 *     apply modify
 *     emit OrderModified
 * Else:
 *     emit OrderRejected
 */