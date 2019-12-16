package sales.display;

import sales.Price;

public interface Display {
    void displayProductNotFound(String missingBarCode);

    void displayScanAgain();

    void displayPrice(Price price);
}
