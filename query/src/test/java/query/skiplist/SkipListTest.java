package query.skiplist;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import query.skiplist.SkipList.SkipNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class SkipListTest {

    @Test
    public void insert_small_number_of_keys_in_order() {

        SkipList<Integer, Integer> list = new SkipList<>();

        IntStream
                .range(0, 10_000).forEach(x -> list.insert(x, x));

        assertAll(
                () -> assertEquals(10_000, list.size()),
                () -> {
                    Iterator<SkipNode<Integer, Integer>> itr = list.iterator();
                    int current = itr.next().key;
                    while (itr.hasNext()) {
                        int next = itr.next().key;
                        assertTrue(next - current == 1, String.format("Value %s,%s are out of order", current, next));
                        current = next;
                    }
                });
    }


    @Test
    public void insert_small_number_of_keys_in_order_using_multiple_thread() {

        SkipList<Integer, Integer> list = new SkipList<>();

        IntStream
                .range(0, 10_000).parallel().forEach(x -> list.insert(x, x));

        System.out.println(list);

        assertAll(
                () -> assertEquals(10_000, list.size(), () -> sizeErrorMessage(list)),
                () -> {
                    Iterator<SkipNode<Integer, Integer>> itr = list.iterator();
                    int current = itr.next().key;
                    while (itr.hasNext()) {
                        int next = itr.next().key;
                        assertTrue(next - current == 1, String.format("Value %s,%s are out of order", current, next));
                        current = next;
                    }
                });


    }

    private String sizeErrorMessage(SkipList<Integer, Integer> list) {
        List<String> values = new ArrayList<>();
        Iterator<SkipNode<Integer, Integer>> itr = list.iterator();
        int current = itr.next().key;
        while (itr.hasNext()) {
            int next = itr.next().key;
            if (next - current != 1) {
                values.add(String.format("(%s;%s)", current, next));
            }
            current = next;
        }
        return values.toString();
    }

}
