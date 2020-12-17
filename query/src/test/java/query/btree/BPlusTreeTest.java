package query.btree;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BPlusTreeTest {

    @Test
    public void search_values() {

        BPlusTree<String, String> tree = new BPlusTree<>();

        tree.put("1", "1");
        tree.put("A", "A");
        tree.put("2", "2");
        tree.put("C", "C");
        tree.put("D", "D");
        tree.put("B", "B");
        tree.put("AA", "AA");


        assertAll(
                () -> assertEquals("A", tree.get("A")),
                () -> assertEquals("B", tree.get("B")),
                () -> assertEquals("C", tree.get("C")),
                () -> assertEquals("D", tree.get("D")),
                () -> assertEquals("2", tree.get("2")),
                () -> assertEquals("AA", tree.get("AA")),
                () -> assertEquals("1", tree.get("1"))
        );

        tree.forEach((k, v) -> System.out.println(k + "->" + v));

    }

    @Test
    public void less_then_values() {

        BPlusTree<String, String> tree = new BPlusTree<>();

        tree.put("1", "1");
        tree.put("A", "A");
        tree.put("2", "2");
        tree.put("C", "C");
        tree.put("D", "D");
        tree.put("B", "B");
        tree.put("AA", "AA");

        List<String> matchedKeys = new ArrayList<>();
        tree.lt("A", (k, v) -> matchedKeys.add(k));

        Assertions.assertIterableEquals(Arrays.asList("1", "2", "A"), matchedKeys);
    }
}
