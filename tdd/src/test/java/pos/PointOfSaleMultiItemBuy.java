package pos;

import org.junit.jupiter.api.Test;

import java.util.Collections;

import static java.util.Collections.singletonMap;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PointOfSaleMultiItemBuy {

    @Test
    public void zero_items_buy() {

        Display display = new Display();
        MerchantStore store = new MerchantStore(display, null);

        store.onTotal();
        assertEquals("No items selected. Scan again!!!!", display.getText());

    }

    @Test
    public void single_item_buy() {
        Display display = new Display();
        MerchantStore store = new MerchantStore(display, new ProductCatalog(singletonMap("100", "$20")));

        store.onBarCode("100");
        store.onTotal();

        assertEquals("Total: $20", display.getText());

    }

    @Test
    public void single_item_but_invalid_scan() {
        Display display = new Display();
        MerchantStore store = new MerchantStore(display, new ProductCatalog(Collections.emptyMap()));

        store.onBarCode("someinvalid");
        store.onTotal();
        assertEquals("No items selected. Scan again!!!!", display.getText());

    }
}
