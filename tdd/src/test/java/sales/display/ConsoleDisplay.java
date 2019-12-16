package sales.display;

import sales.Price;
import sales.PriceFormatProvider;

public class ConsoleDisplay implements Display {
    private String text;

    @Override
    public void displayPrice(float price) {
        this.text = "Total $" + price;
        writeToConsole(this.text);
    }

    private void writeToConsole(String text) {
        System.out.println(text);
    }

    @Override
    public void displayProductNotFound(String missingBarCode) {
        this.text = String.format("Product %s not found. Scan again!", missingBarCode);
        writeToConsole(this.text);
    }

    @Override
    public void displayScanAgain() {
        this.text = "scan again!";
        writeToConsole(this.text);
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void displayPrice(Price price) {
        this.text = "Total " + PriceFormatProvider.format(price);
        writeToConsole(this.text);
    }
}
