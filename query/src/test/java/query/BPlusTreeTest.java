package query;

import org.junit.jupiter.api.Test;
import query.btree.BPlusTree;

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

    }
}
