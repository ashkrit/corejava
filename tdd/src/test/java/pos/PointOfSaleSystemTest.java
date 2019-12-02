package pos;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PointOfSaleSystemTest {

    @Test
    public void return_message_to_display() {
        PointOfSaleSystem pos = new PointOfSaleSystem(new HashMap<>());
        pos.onBarCode("1000000");
        assertEquals("$10", pos.message());
    }


    @Test
    public void handles_multiple_scans_price() {
        Map<String, Double> productPrice = new HashMap<>();
        productPrice.put("1000001", 20.0d);
        productPrice.put("1000002", 30.0d);

        PointOfSaleSystem pos = new PointOfSaleSystem(productPrice);

        pos.onBarCode("1000001");
        assertEquals("$20.00", pos.displayMessage());

        pos.onBarCode("1000002");
        assertEquals("$30.00", pos.displayMessage());
    }
}
