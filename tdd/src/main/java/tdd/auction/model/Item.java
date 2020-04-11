package tdd.auction.model;

public class Item {
    final String itemName;
    final int price;

    public Item(String itemName, int price) {
        this.itemName = itemName;
        this.price = price;
    }

    public String itemName() {
        return itemName;
    }

    public int price() {
        return price;
    }

    public static Item of(String itemName, int price) {
        return new Item(itemName, price);
    }
}
