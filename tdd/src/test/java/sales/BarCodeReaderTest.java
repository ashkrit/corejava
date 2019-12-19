package sales;

import sales.catalog.InMemoryProductCatalog;
import sales.controller.SalesController;
import sales.display.ConsoleDisplay;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;

public class BarCodeReaderTest {

    public static void main(String... args) {

        InMemoryProductCatalog catalog = new InMemoryProductCatalog(new HashMap<String, Price>() {{
            put("1", Price.cents(100));
            put("2", Price.cents(990));
        }});

        SalesController controller = new SalesController(new ConsoleDisplay(), catalog);

        BufferedReader barCodeReader = new BufferedReader(new InputStreamReader(System.in));

        barCodeReader.lines().forEach(line ->
                controller.onBarCode(line)
        );
    }
}
