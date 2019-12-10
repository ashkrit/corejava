package sales;


import org.jmock.Expectations;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.Test;

public class SellOneProductTest {
    private final JUnit5Mockery mockery = new JUnit5Mockery();

    @Test
    public void buy_single_product() {

        ProductCatalog catalog = mockery.mock(ProductCatalog.class);
        Display display = mockery.mock(Display.class);

        String price = "$10.99";

        mockery.checking(new Expectations() {{

            allowing(catalog).findPrice(with("100"));
            will(returnValue(price));

            oneOf(display).displayPrice(with(price));
        }});

        SalesController salesController = new SalesController(display, catalog);
        salesController.onBarCode("100");

        mockery.assertIsSatisfied();

    }

    interface ProductCatalog {
        String findPrice(String barCode);
    }

    interface Display {
        void displayPrice(String priceAsText);
    }

}
