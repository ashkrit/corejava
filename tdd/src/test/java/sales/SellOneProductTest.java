package sales;


import org.jmock.Expectations;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public class SellOneProductTest {
    @RegisterExtension
    JUnit5Mockery context = new JUnit5Mockery();

    @Test
    public void buy_single_product() {

        ProductCatalog catalog = context.mock(ProductCatalog.class);
        Display display = context.mock(Display.class);

        String price = "$10.99";

        context.checking(new Expectations() {{

            allowing(catalog).findPrice(with("100"));
            will(returnValue(price));

            oneOf(display).displayPrice(with(price));
        }});

        SalesController salesController = new SalesController(display, catalog);

        salesController.onBarCode("100");

    }

    interface ProductCatalog {
        String findPrice(String barCode);
    }

    interface Display {
        void displayPrice(String priceAsText);
    }

}
