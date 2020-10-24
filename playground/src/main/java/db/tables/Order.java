package db.tables;

public class Order {
    private final String orderId;
    private final String customerId;
    private final int orderDate;
    private final String status;
    private final double amount;
    private final int noOfItems;

    public Order(String orderId, String customerId, int orderDate, String status, double amount, int noOfItems) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.orderDate = orderDate;
        this.status = status;
        this.amount = amount;
        this.noOfItems = noOfItems;
    }

    public String orderId() {
        return orderId;
    }

    public double amount() {
        return amount;
    }

    public int noOfItems() {
        return noOfItems;
    }

    public int orderDate() {
        return orderDate;
    }

    public String customerId() {
        return customerId;
    }

    public String status() {
        return status;
    }
}
