package sales;

public class SalesController {
    private final SellOneProductTest.Display display;
    private final SellOneProductTest.ProductCatalog catalog;

    public SalesController(SellOneProductTest.Display display, SellOneProductTest.ProductCatalog catalog) {
        this.display = display;
        this.catalog = catalog;
    }

    public void onBarCode(String barCode) {

        display.displayPrice(catalog.findPrice(barCode));

    }
}