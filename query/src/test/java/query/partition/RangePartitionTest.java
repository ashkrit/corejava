package query.partition;

import org.junit.jupiter.api.Test;
import query.partition.RangePartition.PartitionValues;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RangePartitionTest {


    @Test
    public void add_single_value() {
        RangePartition<Integer, String> range = new RangePartition<>();

        range.put(1, "value1");
        PartitionValues<Integer, String> values = range.get(1);

        assertEquals("value1", values.get(1));

    }
}
