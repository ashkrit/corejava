package pos;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PointOfSaleSystemTest {

    @Test
    public void show_price_for_product() {

        Display display = new Display();
        MerchantStore store = new MerchantStore(display);
        store.onBarCode("1234567");

        assertEquals("10.99", display.getText());
    }

    @Test
    public void another_product_scan() {

        Display display = new Display();
        MerchantStore store = new MerchantStore(display);
        store.onBarCode("234567");

        assertEquals("11.99", display.getText());

    }
}
