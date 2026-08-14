package event;

public class TradeDTO {
    /**
     * Pseudo event that contains the vital info required, except the event id and command id
     */

    private final long buyOrderId;
    private final long sellOrderId;
    private final long price;
    private final long quantity;

    public TradeDTO(long buyOrderId, long sellOrderId, long price, long quantity) {
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.price = price;
        this.quantity = quantity;
    }

    public long getBuyOrderId() {
        return buyOrderId;
    }

    public long getSellOrderId() {
        return sellOrderId;
    }

    public long getPrice() {
        return price;
    }

    public long getQuantity() {
        return quantity;
    }
}
