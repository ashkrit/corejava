package pos;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PointOfSaleSystemTest {

    @Test
    public void return_message_to_display() {
        PointOfSaleSystem pos = new PointOfSaleSystem();
        pos.onBarCode("1000000");
        assertEquals("$10", pos.message());
    }
}
