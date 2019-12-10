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

    public void displayProductPrice(String priceAsText) {
        this.text = priceAsText;
    }

    public void noItemsSelected() {
        this.text = "No items selected. Scan again!!!!";
    }

    public void displayTotal(String priceAsText) {
        this.text = "Total: " + priceAsText;
    }
}
