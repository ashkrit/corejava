package sales.display;

public class ConsoleDisplay implements Display {
    private String text;

    @Override
    public void displayPrice(float price) {
        this.text = "Total $" + price;
    }

    @Override
    public void displayProductNotFound(String missingBarCode) {
        this.text = String.format("Product %s not found. Scan again!", missingBarCode);
    }

    @Override
    public void displayScanAgain() {
        this.text = "scan again!";
    }

    @Override
    public String getText() {
        return text;
    }
}
