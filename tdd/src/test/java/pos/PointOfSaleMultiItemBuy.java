package pos;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PointOfSaleMultiItemBuy {

    @Test
    public void zero_items_buy() {

        Display display = new Display();
        MerchantStore store = new MerchantStore(display, null);

        store.onTotal();
        assertEquals("No items selected. Scan again!!!!", display.getText());

    }
}
