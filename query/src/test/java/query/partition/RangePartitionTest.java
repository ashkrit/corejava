package query.partition;

import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class RangePartitionTest {


    @Test
    public void add_single_value() {
        RangePartition<Integer, String> range = new RangePartition<>();

        range.put(1, "value1");

        assertEquals("value1", range.getValue(1));

    }


    @Test
    public void add_multiple_values() {
        RangePartition<Integer, String> range = new RangePartition<>();

        range.put(1, "value1");
        range.put(2, "value2");
        range.put(10, "value10");

        assertAll(
                () -> assertEquals("value1", range.getValue(1)),
                () -> assertEquals("value2", range.getValue(2)),
                () -> assertEquals("value10", range.getValue(10))
        );

    }

    @Test
    public void check_node_split() {

        RangePartition<Integer, String> range = new RangePartition<>(5);

        IntStream
                .range(0, 5)
                .forEach(r -> range.put(r, "value" + r));

        IntStream
                .range(5, 10)
                .forEach(r -> range.put(r, "value" + r));

        assertNotEquals(range.getPartition(0).name, range.getPartition(8).name);
    }
}
