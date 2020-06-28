package faas;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FunctionTest {


    @Test
    public void square_number() {

        Function<Integer, Integer> sqr = x -> x * x;

        assertEquals(4, sqr.apply(2));
        assertEquals(9, sqr.apply(3));
        assertEquals(16, sqr.apply(4));
    }

    @Test
    public void to_upper() {
        Function<String, String> upper = x -> x.toUpperCase();
        assertEquals("FAAS", upper.apply("FaaS"));
    }


    @Test()
    public void save_function_fails() {
        assertThrows(NotSerializableException.class, () -> {
            Function<String, String> upper = x -> x.toUpperCase();
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                 ObjectOutputStream os = new ObjectOutputStream(bos)) {
                os.writeObject(upper);
            }
        });
    }


    @Test()
    public void save_function_works() throws Exception {

        Function<String, String> upper = (Function<String, String> & Serializable) x -> x.toUpperCase();

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream os = new ObjectOutputStream(bos)) {

            os.writeObject(upper);

            try (ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
                 ObjectInputStream in = new ObjectInputStream(bis)) {

                Function<String, String> restoredUpper = (Function<String, String>) in.readObject();

                Assertions.assertEquals("FAAS", restoredUpper.apply("FaaS"));
            }

        }
    }

}
