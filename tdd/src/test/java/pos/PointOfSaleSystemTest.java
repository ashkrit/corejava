package pos;

import org.junit.jupiter.api.Test;

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

}
