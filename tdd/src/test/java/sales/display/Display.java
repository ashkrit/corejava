package sales.display;

import sales.Price;

public interface Display {
    void displayPrice(float price);

    void displayProductNotFound(String missingBarCode);

    void displayScanAgain();

    String getText();

    void displayPrice(Price price);
}
