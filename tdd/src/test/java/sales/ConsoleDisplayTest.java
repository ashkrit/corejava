package sales;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sales.display.ConsoleDisplay;
import sales.display.Display;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class ConsoleDisplayTest {
    private PrintStream currentSysOut;

    @BeforeEach
    public void overrideSystemOut() {
        this.currentSysOut = System.out;
    }

    @AfterEach
    public void restoreSystemOut() {
        System.setOut(currentSysOut);
    }

    private List<String> lines(String sinkText) {
        return Arrays.asList(sinkText.split(System.lineSeparator()));
    }


    @Test
    public void display_price() {

        ByteArrayOutputStream sink = new ByteArrayOutputStream();
        System.setOut(new PrintStream(sink));

        Display display = createDisplayDevice();
        display.displayPrice(10.99f);

        String sinkText = sink.toString();
        assertEquals(Arrays.asList("Total $10.99"), lines(sinkText));
    }

    @Test
    public void display_value_using_price_object() {
        ByteArrayOutputStream sink = new ByteArrayOutputStream();
        System.setOut(new PrintStream(sink));

        Display display = createDisplayDevice();
        display.displayPrice(Price.cents(1098));

        String sinkText = sink.toString();
        assertEquals(Arrays.asList("Total $10.98"), lines(sinkText));
    }

    @Test
    public void display_product_not_found() {

        ByteArrayOutputStream sink = new ByteArrayOutputStream();
        System.setOut(new PrintStream(sink));

        Display display = createDisplayDevice();
        display.displayProductNotFound("P009");

        String sinkText = sink.toString();
        assertEquals(Arrays.asList("Product P009 not found. Scan again!"), lines(sinkText));
    }

    @Test
    public void display_scan_again() {

        ByteArrayOutputStream sink = new ByteArrayOutputStream();
        System.setOut(new PrintStream(sink));

        Display display = createDisplayDevice();
        display.displayScanAgain();

        String sinkText = sink.toString();
        assertEquals(Arrays.asList("scan again!"), lines(sinkText));

    }

    private Display createDisplayDevice() {
        return new ConsoleDisplay();
    }
}
