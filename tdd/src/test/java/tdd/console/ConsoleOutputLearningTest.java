package tdd.console;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConsoleOutputLearningTest {

    @Test
    public void showsMessageOnConsole() {

        ByteArrayOutputStream bos = overrideSysOut();

        System.out.println("How are you");

        String[] lines = new String(bos.toByteArray()).split("\r\n");

        assertEquals("How are you", lines[0]);

    }

    private ByteArrayOutputStream overrideSysOut() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bos));
        return bos;
    }
}
