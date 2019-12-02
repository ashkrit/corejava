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

}
