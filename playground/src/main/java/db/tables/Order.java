package db.tables;

public class Order {
    private final long orderId;
    private final String customerId;
    private final int orderDate;
    private final String status;
    private final double amount;
    private final int noOfItems;

    public Order(long orderId, String customerId, int orderDate, String status, double amount, int noOfItems) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.orderDate = orderDate;
        this.status = status;
        this.amount = amount;
        this.noOfItems = noOfItems;
    }

    public static Order of(long orderId, String customerId, int orderDate, String status, double amount, int noOfItems) {
        return new Order(orderId, customerId, orderDate, status, amount, noOfItems);
    }

    public long orderId() {
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

    @Override
    public String toString() {
        return String.format("OrderId:%s; customerId:%s; orderDate:%s; status:%s; %s;%s", orderId, customerId, orderDate, status, amount, noOfItems);
    }
}
