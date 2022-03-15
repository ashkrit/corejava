package heap;

import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public class Heap<T extends Comparable> {

    private final AtomicLong id = new AtomicLong();
    private final NavigableSet<Item> data;
    private final HeapType heapType;

    public void add(T value) {
        data.add(new Item(value, id.incrementAndGet()));
    }

    public Stream<T> stream() {
        return data
                .stream()
                .map(v -> v.value);
    }

    public Stream<T> top(int x) {
        return stream().limit(x);
    }

    class Item implements Comparable<Item> {
        private final T value;
        private final long index;

        @Override
        public int compareTo(Heap<T>.Item o) {

            int r = this.value.compareTo(o.value);
            r = heapType.equals(HeapType.Max) ? -r : r;
            if (r != 0) {
                return r;
            }
            return Long.compare(index, o.index);
        }

        Item(T value, long index) {
            this.value = value;
            this.index = index;
        }

    }

    public Heap(NavigableSet<Item> data, HeapType heapType) {
        this.data = data;
        this.heapType = heapType;
    }

    public enum HeapType {
        Min,
        Max
    }

    public static <T extends Comparable> Heap<T> newSingleThread(HeapType type) {
        return new Heap<>(new TreeSet<>(), type);
    }

    public static <T extends Comparable> Heap<T> newMultiThread(HeapType type) {
        return new Heap<>(new ConcurrentSkipListSet<>(), type);
    }
}
