package distributed.navigable;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class NavigableMapLearningTest {

    @Test
    public void return_value_of_key() {

        NavigableMap<Integer, byte[]> buffers = new TreeMap<Integer, byte[]>() {{
            put(10, new byte[10]);
            put(20, new byte[20]);
            put(9, new byte[9]);
            put(27, new byte[27]);
            put(99, new byte[99]);
            put(55, new byte[55]);
        }};

        assertEquals(99, buffers.get(99).length);
    }

    @Test
    public void ceiling_returns_entry_same_as_input_key_if_it_exists() {

        NavigableMap<Integer, byte[]> buffers = new TreeMap<Integer, byte[]>() {{
            put(10, new byte[10]);
            put(20, new byte[20]);
            put(9, new byte[9]);
            put(27, new byte[27]);
            put(99, new byte[99]);
            put(55, new byte[55]);
        }};

        System.out.println(buffers);
        Map.Entry<Integer, byte[]> cellEntry = buffers.ceilingEntry(27);
        assertEquals(27, cellEntry.getKey());
    }

    @Test
    public void ceiling_returns_entry_greater_than_input_key_if_key_does_not_exists() {

        NavigableMap<Integer, byte[]> buffers = new TreeMap<Integer, byte[]>() {{
            put(10, new byte[10]);
            put(20, new byte[20]);
            put(9, new byte[9]);
            put(27, new byte[27]);
            put(99, new byte[99]);
            put(55, new byte[55]);
        }};

        System.out.println(buffers);
        Map.Entry<Integer, byte[]> cellEntry = buffers.ceilingEntry(29);
        assertEquals(55, cellEntry.getKey());
    }

    @Test
    public void ceiling_returns_null_when_input_is_the_max_key() {

        NavigableMap<Integer, byte[]> buffers = new TreeMap<Integer, byte[]>() {{
            put(10, new byte[10]);
            put(20, new byte[20]);
            put(9, new byte[9]);
            put(27, new byte[27]);
            put(99, new byte[99]);
            put(55, new byte[55]);
        }};

        Map.Entry<Integer, byte[]> cellEntry = buffers.ceilingEntry(100);
        Assertions.assertTrue(cellEntry == null);
    }

    @Test
    public void floor_return_same_entry_when_key_exits() {
        NavigableMap<Integer, byte[]> buffers = new TreeMap<Integer, byte[]>() {{
            put(10, new byte[10]);
            put(20, new byte[20]);
            put(9, new byte[9]);
            put(27, new byte[27]);
            put(99, new byte[99]);
            put(55, new byte[55]);
        }};

        System.out.println(buffers);
        Map.Entry<Integer, byte[]> cellEntry = buffers.floorEntry(27);
        assertEquals(27, cellEntry.getKey());
    }

    @Test
    public void floor_returns_entry_less_than_input_key_if_key_does_not_exists() {

        NavigableMap<Integer, byte[]> buffers = new TreeMap<Integer, byte[]>() {{
            put(10, new byte[10]);
            put(20, new byte[20]);
            put(9, new byte[9]);
            put(27, new byte[27]);
            put(99, new byte[99]);
            put(55, new byte[55]);
        }};

        System.out.println(buffers);
        Map.Entry<Integer, byte[]> cellEntry = buffers.floorEntry(29);
        assertEquals(27, cellEntry.getKey());
    }


    @Test
    public void floor_returns_max_value_when_input_key_is_greater_than_max_value() {

        NavigableMap<Integer, byte[]> buffers = new TreeMap<Integer, byte[]>() {{
            put(10, new byte[10]);
            put(20, new byte[20]);
            put(9, new byte[9]);
            put(27, new byte[27]);
            put(99, new byte[99]);
            put(55, new byte[55]);
        }};

        Map.Entry<Integer, byte[]> cellEntry = buffers.floorEntry(100);
        assertEquals(99, cellEntry.getKey());
    }


    @Test
    public void head_returns_all_elements_less_than_input_key() {

        NavigableMap<Integer, byte[]> buffers = new TreeMap<Integer, byte[]>() {{
            put(10, new byte[10]);
            put(20, new byte[20]);
            put(9, new byte[9]);
            put(27, new byte[27]);
            put(99, new byte[99]);
            put(55, new byte[55]);
        }};

        SortedMap<Integer, byte[]> smallerElements = buffers.headMap(15);
        assertArrayEquals(new Integer[]{9, 10}, smallerElements.keySet().toArray(new Integer[]{}));
    }

    @Test
    public void tail_returns_all_elements_greater_than_input_key() {

        NavigableMap<Integer, byte[]> buffers = new TreeMap<Integer, byte[]>() {{
            put(10, new byte[10]);
            put(20, new byte[20]);
            put(9, new byte[9]);
            put(27, new byte[27]);
            put(99, new byte[99]);
            put(55, new byte[55]);
        }};

        SortedMap<Integer, byte[]> biggerElements = buffers.tailMap(15);
        assertArrayEquals(new Integer[]{20, 27, 55, 99}, biggerElements.keySet().toArray(new Integer[]{}));
    }

}
