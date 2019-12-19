package sales.display;

import sales.Price;
import sales.PriceFormatProvider;

public class ConsoleDisplay implements Display {

    private void writeToConsole(String text) {
        System.out.println(text);
    }

    @Override
    public void displayProductNotFound(String missingBarCode) {
        writeToConsole(String.format("Product %s not found. Scan again!", missingBarCode));
    }

    @Override
    public void displayScanAgain() {
        writeToConsole("scan again!");
    }


    @Override
    public void displayPrice(Price price) {
        writeToConsole("Total " + PriceFormatProvider.format(price));
    }
}
