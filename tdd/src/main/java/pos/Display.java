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
}
