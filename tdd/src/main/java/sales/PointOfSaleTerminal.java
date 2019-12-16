package sales;

import sales.catalog.InMemoryProductCatalog;
import sales.controller.SalesController;
import sales.display.ConsoleDisplay;

import java.util.HashMap;

public class PointOfSaleTerminal {

    public static void main(String... args) {

        SalesController controller = new SalesController(new ConsoleDisplay(),
                new InMemoryProductCatalog(new HashMap<String, Price>() {{
                    put("100", Price.cents(499));
                    put("200", Price.cents(1199));
                }}));

        controller.onBarCode("100");
        controller.onBarCode("200");
        controller.onBarCode(":Missing:");
        controller.onBarCode(null);
        controller.onBarCode("");

    }
}
