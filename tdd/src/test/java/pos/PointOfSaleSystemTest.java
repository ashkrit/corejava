package pos;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PointOfSaleSystemTest {

    @Test
    public void show_price_for_product() {

        Display display = new Display();
        MerchantStore store = new MerchantStore();
        store.onBarCode("1234567");

        Assertions.assertEquals("10.99", display.getText());
    }
}
