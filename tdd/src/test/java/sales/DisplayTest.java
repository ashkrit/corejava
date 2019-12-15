package sales;

import org.junit.jupiter.api.Test;
import sales.display.ConsoleDisplay;
import sales.display.Display;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class DisplayTest {

    @Test
    public void display_price() {

        Display display = createDisplayDevice();
        display.displayPrice(10.99f);
        assertEquals("Total $10.99", display.getText());
    }

    @Test
    public void display_product_not_found() {

        Display display = createDisplayDevice();
        display.displayProductNotFound("P009");
        assertEquals("Product P009 not found. Scan again!", display.getText());
    }

    @Test
    public void display_scan_again() {

        Display display = createDisplayDevice();
        display.displayScanAgain();
        assertEquals("scan again!", display.getText());
    }

    private ConsoleDisplay createDisplayDevice() {
        return new ConsoleDisplay();
    }
}
