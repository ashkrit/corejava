package pos;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PointOfSaleSystemTest {

    @Test
    public void null_barcode_scan() {
        DisplayDevice display = new ScreenDisplay();

        MerchantStore store = new MerchantStore(display);
        store.onBarCode(null);

        assertEquals("scan again", display.message());
    }


    @Test
    public void empty_barcode_scan() {
        DisplayDevice display = new ScreenDisplay();

        MerchantStore store = new MerchantStore(display);
        store.onBarCode("");
        assertEquals("scan again", display.message());
    }

    @Test
    public void whitespace_barcode_scan() {
        DisplayDevice display = new ScreenDisplay();

        MerchantStore store = new MerchantStore(display);
        store.onBarCode("   ");
        assertEquals("scan again", display.message());
    }

    @Test
    public void valid_barcode_scan_but_barcode_does_not_exists() {
        DisplayDevice display = new ScreenDisplay();

        MerchantStore store = new MerchantStore(display);
        store.onBarCode("123000");
        assertEquals("invalid barcode", display.message());
    }

    @Test
    public void shows_price_based_on_barcode_scan() {
        DisplayDevice display = new ScreenDisplay();
        Map<String, Double> productPrice = new HashMap<>();
        productPrice.put("123001", 10.99);
        productPrice.put("123002", 11.99);
        MerchantStore store = new MerchantStore(productPrice, display);
        store.onBarCode("123001");
        assertEquals("$10.99", display.message());
    }

    @Test
    public void handle_multiple_barcode_scan() {
        DisplayDevice display = new ScreenDisplay();
        Map<String, Double> productPrice = new HashMap<>();
        productPrice.put("123001", 10.99);
        productPrice.put("123002", 11.99);
        MerchantStore store = new MerchantStore(productPrice, display);

        store.onBarCode("123001");
        assertEquals("$10.99", display.message());

        store.onBarCode("123002");
        assertEquals("$11.99", display.message());
    }

}
