package stream.ch006.morecollectors;

import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static java.util.Arrays.parallelPrefix;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class DeltaEncodingTest {

    @Test
    public void delta_encoding() {
        int[] q = {1, 2, 3, 4, 5, 2, 4, 6, 8, 10};

        //Encode
        int[] w = new int[q.length];
        IntStream.range(0, q.length).parallel().forEach(index -> {
            if (index == 0) {
                w[index] = q[index];
            } else {
                w[index] = q[index] - q[index - 1];
            }
        });

        //Decode
        parallelPrefix(w, Integer::sum);
        assertArrayEquals(q, w);
    }

}
