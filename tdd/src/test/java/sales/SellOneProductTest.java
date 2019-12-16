package sales;


import org.jmock.Expectations;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import sales.catalog.ProductCatalog;
import sales.controller.SalesController;
import sales.display.Display;

public class SellOneProductTest {
    @RegisterExtension
    JUnit5Mockery context = new JUnit5Mockery();

    @Test
    public void buy_single_product() {

        ProductCatalog catalog = context.mock(ProductCatalog.class);
        Display display = context.mock(Display.class);

        Price priceCents = Price.cents(1099);

        context.checking(new Expectations() {{

            allowing(catalog).findPriceAsCents(with("100"));
            will(returnValue(priceCents));

            oneOf(display).displayPrice(with(priceCents));
        }});

        SalesController salesController = new SalesController(display, catalog);

        salesController.onBarCode("100");

    }

    @Test
    public void product_not_found() {

        ProductCatalog catalog = context.mock(ProductCatalog.class);
        Display display = context.mock(Display.class);

        context.checking(new Expectations() {{

            allowing(catalog).findPriceAsCents(with("$invalid_bar_code$"));
            will(returnValue(null));

            oneOf(display).displayProductNotFound("$invalid_bar_code$");
        }});

        SalesController salesController = new SalesController(display, catalog);

        salesController.onBarCode("$invalid_bar_code$");

    }

    @Test
    public void null_bar_code_scan() {

        ProductCatalog catalog = context.mock(ProductCatalog.class);
        Display display = context.mock(Display.class);
        context.checking(new Expectations() {{
            ignoring(catalog);

            oneOf(display).displayScanAgain();
        }});

        SalesController salesController = new SalesController(display, catalog);
        salesController.onBarCode(null);
    }

    @Test
    public void empty_bar_code_scan() {

        Display display = context.mock(Display.class);
        context.checking(new Expectations() {{
            oneOf(display).displayScanAgain();
        }});

        SalesController salesController = new SalesController(display, null);
        salesController.onBarCode("    ");
    }

}
