package db.tables;

public class Order {
    private final String orderId;

    public Order(String orderId) {
        this.orderId = orderId;
    }

    public String orderId() {
        return orderId;
    }
}
