package sales.controller;

import sales.SellOneProductTest;
import sales.catalog.ProductCatalog;

public class SalesController {
    private final SellOneProductTest.Display display;
    private final ProductCatalog catalog;

    public SalesController(SellOneProductTest.Display display, ProductCatalog catalog) {
        this.display = display;
        this.catalog = catalog;
    }

    public void onBarCode(String barCode) {

        if (barCode == null || barCode.trim().isEmpty()) {
            display.displayScanAgain();
            return;
        }

        Float price = catalog.findPrice(barCode);
        if (price == null) {
            display.displayProductNotFound(barCode);
        } else {
            display.displayPrice(price);
        }
    }
}
