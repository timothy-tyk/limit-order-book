package validation;

public enum OrderRejectedReason {
    UNKNOWN_ORDER,
    DUPLICATED_ORDER_ID,
    INVALID_PRICE,
    INVALID_QTY,
    NO_LIQUIDITY
}
