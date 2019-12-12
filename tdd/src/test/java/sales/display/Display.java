package sales.display;

public interface Display {
    void displayPrice(float price);

    void displayProductNotFound(String missingBarCode);

    void displayScanAgain();
}
