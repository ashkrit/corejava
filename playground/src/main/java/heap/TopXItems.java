package heap;

import heap.Heap.HeapType;

import java.util.Random;
import java.util.stream.IntStream;

public class TopXItems {

    public static void main(String[] args) {
        Heap<SalesItem> heap = Heap.newMultiThread(HeapType.Min);
        IntStream
                .range(1, 10)
                .mapToObj(x -> new SalesItem("item" + new Random().nextInt(x), new Random().nextInt(1000)))
                .forEach(heap::add);

        heap.stream().forEach(System.out::println);
    }


    static class SalesItem implements Comparable<SalesItem> {

        private final String product;
        private final long sales;

        @Override
        public int compareTo(SalesItem o) {
            return Long.compare(sales, o.sales);
        }

        public SalesItem(String itemName, long times) {
            this.product = itemName;
            this.sales = times;
        }

        @Override
        public String toString() {
            return "Item{" +
                    "itemName='" + product + '\'' +
                    ", times=" + sales +
                    '}';
        }
    }
}
