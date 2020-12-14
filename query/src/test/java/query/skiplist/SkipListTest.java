package query.skiplist;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import query.skiplist.SkipList.SkipNode;

import java.util.Iterator;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SkipListTest {

    @Test
    public void insert_small_number_of_keys_in_order() {

        SkipList<Integer, Integer> list = new SkipList<>();
        IntStream
                .range(0, 10_000).forEach(x -> list.insert(x, x));

        Assertions.assertAll(
                () -> {
                    Iterator<SkipNode<Integer, Integer>> itr = list.iterator();
                    int count = 0;
                    while (itr.hasNext()) {
                        itr.next();
                        count++;
                    }
                    assertEquals(10_000, count);
                },
                () -> {
                    Iterator<SkipNode<Integer, Integer>> itr = list.iterator();
                    int current = itr.next().key;
                    while (itr.hasNext()) {
                        int next = itr.next().key;
                        assertTrue(next > current, String.format("Value %s should be after %s", next, current));
                    }
                });
    }
}
