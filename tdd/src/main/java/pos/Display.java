package pos;

public class Display {
    private String text;

    public String getText() {
        return text;
    }

    public void displayProductNotFund(String barCode) {
        this.text = "product not found " + barCode;
    }

    public void displayInvalidScan() {
        this.text = "invalid scan";
    }

    public void displayProductPrice(int priceInCents) {
        this.text = formatPrice(to$(priceInCents));
    }

    public void noItemsSelected() {
        this.text = "No items selected. Scan again!!!!";
    }

    public void displayTotal(int priceAsCents) {
        this.text = "Total: " + formatPrice(to$(priceAsCents));
    }

    private String formatPrice(float priceInCents) {
        return String.format("$%.2f", priceInCents);
    }

    private float to$(int priceInCents) {
        return priceInCents / 100f;
    }
}
