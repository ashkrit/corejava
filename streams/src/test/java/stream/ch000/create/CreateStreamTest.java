package stream.ch000.create;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("How to create Stream")
public class CreateStreamTest {

    @Test
    void stream_from_collection() {
        List<String> values = Arrays.asList("A", "B");
        assertTrue(Stream.class.isInstance(values.stream()));
    }

    @Test
    void stream_from_stream_factory_args() {
        Object values = Stream.of("A", "B");
        assertTrue(Stream.class.isInstance(values));
    }

    @Test
    void stream_from_stream_factory_array() {
        Object values = Stream.of(new String[]{"A", "B"});
        assertTrue(Stream.class.isInstance(values));
    }

    @Test
    void stream_from_stream_factory_supplier() {
        Object values = Stream.generate(() -> new String[]{"A", "B"});
        assertTrue(Stream.class.isInstance(values));
    }

    @Test
    void stream_from_stream_factory_iterate() {
        Object values = Stream.iterate(0, x -> x + 1);
        assertTrue(Stream.class.isInstance(values));
    }

    @Test
    void primitive_stream() {
        Object values = IntStream.of(1, 2, 3); // Double,Float,Long
        assertTrue(IntStream.class.isInstance(values));
    }


    @Test
    void io_stream() throws Exception {
        File f = File.createTempFile("xyz", "y");
        Object values = new BufferedReader(new FileReader(f)).lines();

        assertTrue(Stream.class.isInstance(values));

        values = Files.lines(f.toPath());
        assertTrue(Stream.class.isInstance(values));

    }

}
