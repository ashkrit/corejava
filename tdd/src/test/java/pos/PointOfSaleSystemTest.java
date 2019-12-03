package pos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PointOfSaleSystemTest {

    private Display display;
    private MerchantStore store;

    @BeforeEach
    public void setup() {
        this.display = new Display();
        this.store = new MerchantStore(display, new HashMap<String, String>() {
            {
                put("1234567", "10.99");
                put("234567", "11.99");
            }
        });
    }

    @Test
    public void show_price_for_product() {

        store.onBarCode("1234567");

        assertEquals("10.99", display.getText());
    }

    @Test
    public void another_product_scan() {

        store.onBarCode("234567");
        assertEquals("11.99", display.getText());
    }

    @Test
    public void invalid_product_scan() {

        store.onBarCode("888888");

        assertEquals("product not found 888888", display.getText());
    }

    @Test
    public void null_product_scan() {
        Display display = new Display();
        MerchantStore store = new MerchantStore(display, null);

        store.onBarCode(null);

        assertEquals("invalid scan", display.getText());
    }

    @Test
    public void empty_product_scan() {
        Display display = new Display();
        MerchantStore store = new MerchantStore(display, null);

        store.onBarCode("");
        assertEquals("invalid scan", display.getText());
    }
}
