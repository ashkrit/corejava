package query.skiplist;


import java.util.Iterator;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class SkipList<K extends Comparable, V> implements Iterable<SkipList.SkipNode<K, V>> {

    private SkipNodeHead<K, V> root = new SkipNodeHead<>(null);

    public void insert(K key, V value) {

        SkipNode<K, V> node = new SkipNode<>(key, value, 0, null);
        SkipNode<K, V> headNode = root.head.get();
        if (headNode == null) {
            if (!root.casHead(null, node)) {
                insertKey(node);
            }
        } else {
            insertKey(node);
        }
    }

    private void insertKey(SkipNode<K, V> newNode) {

        while (true) {
            SkipNode<K, V> currentNode = root.head.get();
            SkipNode<K, V> previousNode = null;
            while (currentNode != null) {
                int matchValue = newNode.key.compareTo(currentNode.key);
                if (matchValue == 0) {
                    return;
                } else if (matchValue > 0) {
                    previousNode = currentNode;
                    currentNode = currentNode.nextNode();
                } else if (matchValue < 0) {
                    newNode.casNext(null, currentNode);
                    if (previousNode == null) {
                        if (root.casHead(currentNode, newNode)) {
                            return;
                        } else {
                            //CAS failed , try again
                            System.out.printf("Old %s, New %s", currentNode, newNode);
                            currentNode = root.head.get();
                            previousNode = null;
                        }
                    } else if (previousNode != null) {
                        if (previousNode.casNext(currentNode, newNode)) {
                            return;
                        } else {
                            //CAS failed , try again
                            System.out.printf("Previous -> Old %s, New %s", currentNode, newNode);
                            currentNode = root.head.get();
                            previousNode = null;
                        }
                    }
                }
            }
            if (previousNode.casNext(currentNode, newNode)) {
                return;
            }
        }
    }

    @Override
    public Iterator<SkipNode<K, V>> iterator() {

        return new Iterator<SkipNode<K, V>>() {
            AtomicReference<SkipNode<K, V>> node = root.head;

            @Override
            public boolean hasNext() {
                return node.get() != null;
            }

            @Override
            public SkipNode<K, V> next() {
                SkipNode<K, V> v = node.get();
                node = node.get().next;
                return v;
            }
        };
    }

    @Override
    public void forEach(Consumer<? super SkipNode<K, V>> action) {

    }

    @Override
    public Spliterator<SkipNode<K, V>> spliterator() {
        return null;
    }


    public static class SkipNode<K extends Comparable, V> {
        public final V value;
        public final K key;
        public final int level;
        public AtomicReference<SkipNode<K, V>> next;

        public SkipNode(K key, V value, int level, SkipNode<K, V> next) {
            this.key = key;
            this.value = value;
            this.level = level;
            this.next = new AtomicReference<>(next);
        }

        final boolean casNext(SkipNode<K, V> current, SkipNode<K, V> next) {
            boolean r = this.next.compareAndSet(current, next);
            if (r) {
                //System.out.println("Done " + next);
            }
            return r;
        }

        final SkipNode<K, V> nextNode() {
            return next.get();
        }

        @Override
        public String toString() {
            return String.format("Key:%s,Level %s", key, level);
        }
    }

    public static class SkipNodeHead<K extends Comparable, V> {
        public final AtomicReference<SkipNode<K, V>> head;

        public SkipNodeHead(SkipNode<K, V> head) {
            this.head = new AtomicReference<>(head);
        }

        final boolean casHead(SkipNode<K, V> current, SkipNode<K, V> next) {
            return this.head.compareAndSet(current, next);
        }

        public SkipNode<K, V> value() {
            return head.get();
        }
    }


    long size() {
        Iterator<SkipNode<K, V>> itr = iterator();
        long count = 0;
        while (itr.hasNext()) {
            itr.next();
            count++;
        }
        return count;
    }
}
