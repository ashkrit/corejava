package sales;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.IntStream.range;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SystemOutTest {

    private PrintStream currentSysOut;

    @BeforeEach
    public void overrideSystemOut() {
        this.currentSysOut = System.out;
    }

    @AfterEach
    public void restoreSystemOut() {
        System.setOut(currentSysOut);
    }

    @Test
    public void single_line_write() {

        ByteArrayOutputStream sink = new ByteArrayOutputStream();
        System.setOut(new PrintStream(sink));

        System.out.println("Say hello.");

        String sinkText = sink.toString();
        assertEquals(Arrays.asList("Say hello."), lines(sinkText));
    }

    @Test
    public void multiple_line_write() {

        ByteArrayOutputStream sink = new ByteArrayOutputStream();
        System.setOut(new PrintStream(sink));

        range(0, 3)
                .forEach(i -> System.out.println("Say hello" + i));

        String sinkText = sink.toString();
        assertEquals(Arrays.asList("Say hello0", "Say hello1", "Say hello2"), lines(sinkText));
    }

    private List<String> lines(String sinkText) {
        return Arrays.asList(sinkText.split(System.lineSeparator()));
    }

    @Test
    public void manual_test_for_sysout() {
        System.out.println("Say hello for test.");
    }
}
