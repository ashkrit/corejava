package pos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PointOfSaleSystemTest {

    private final Map<String, Double> productPrice = new HashMap<>();

    @BeforeEach
    public void init() {
        productPrice.put("1000001", 20d);
        productPrice.put("1000002", 30d);
    }

    @Test
    public void handles_missing_barcode_price() {
        PointOfSaleSystem pos = new PointOfSaleSystem(new HashMap<>());
        pos.onBarCode("1000000");
        assertEquals("-1", pos.displayMessage());
    }

    @Test
    public void handles_multiple_scans_price() {
        PointOfSaleSystem pos = new PointOfSaleSystem(productPrice);

        pos.onBarCode("1000001");
        assertEquals("$20.00", pos.displayMessage());

        pos.onBarCode("1000002");
        assertEquals("$30.00", pos.displayMessage());
    }
}
