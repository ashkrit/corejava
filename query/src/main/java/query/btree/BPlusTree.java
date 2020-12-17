package query.btree;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

public class BPlusTree<K extends Comparable<K>, V> {

    public static final int MAX_CHILDREN = 4;
    private Node<K, V> root;
    private int size;
    private int height;

    public BPlusTree() {
        this.root = new Node<>(0);
    }

    public void put(K key, V value) {
        Node<K, V> n = insert(root, key, value, height);
        size++;
        if (n == null) return;
        linkNewNode(n);
    }

    private void linkNewNode(Node<K, V> n) {
        Node<K, V> newRoot = new Node<>(2);
        newRoot.entries[0] = new NodeEntry<>(root.entries[0].key, null, root);
        newRoot.entries[1] = new NodeEntry<>(n.entries[0].key, null, n);
        root = newRoot;
        height++;
    }

    private Node<K, V> insert(Node<K, V> r, K key, V value, int height) {
        int position;
        NodeEntry<K, V> t = new NodeEntry<>(key, value, null);
        if (height == 0) {
            for (position = 0; position < r.childCount; position++) {
                if (lessThan(key, r.entries[position].key)) {
                    break;
                }
            }
        } else {
            for (position = 0; position < r.childCount; position++) {
                int nextElement = position + 1;
                if (r.isLast(nextElement) || lessThan(key, r.entries[nextElement].key)) {

                    Node<K, V> c = insert(r.entries[position++].next, key, value, height - 1);
                    if (c == null) return null;

                    t.key = c.entries[0].key;
                    t.value = null;
                    t.next = c;
                    break;
                }
            }
        }

        shiftElementToEnd(r, position);

        r.entries[position] = t;
        r.childCount++;

        if (r.childCount < MAX_CHILDREN) {
            return null;
        } else {
            return split(r);
        }
    }

    private void shiftElementToEnd(Node<K, V> r, int x) {
        int noOfElementToCopy = r.childCount - x;
        if (noOfElementToCopy >= 0) {
            System.arraycopy(r.entries, x, r.entries, x + 1, noOfElementToCopy);
        }
    }

    private Node<K, V> split(Node<K, V> r) {
        int newChildCount = MAX_CHILDREN / 2;
        r.childCount = newChildCount;

        Node<K, V> splitNode = new Node<>(newChildCount);
        System.arraycopy(r.entries, newChildCount, splitNode.entries, 0, newChildCount);
        return splitNode;
    }

    public boolean lessThan(K key, K key1) {
        return key.compareTo(key1) < 0;
    }

    public boolean eq(K key, K key1) {
        return key.compareTo(key1) == 0;
    }

    public V get(K key) {
        return search(root, key, height);
    }

    public void lt(K key, BiConsumer<K, V> consumer) {
        _search(key, root, height, consumer, this.lte);
    }

    public void gt(K key, BiConsumer<K, V> consumer) {
        _search(key, root, height, consumer, this.gte);
    }

    private V search(Node<K, V> node, K key, int height) {
        NodeEntry<K, V>[] child = node.entries;
        if (height == 0) {
            for (int index = 0; index < node.childCount; index++) {
                if (key.compareTo(child[index].key) == 0) {
                    return child[index].value;
                }
            }
        } else {
            for (int index = 0; index < node.childCount; index++) {
                if (node.isLast(index + 1) || lessThan(key, node.entries[index + 1].key)) {
                    return search(node.entries[index].next, key, height - 1);
                }
            }
        }
        return null;
    }

    private void _search(K key, Node<K, V> node, int height, BiConsumer<K, V> consumer, BiPredicate<K, K> predicate) {

        NodeEntry<K, V>[] child = node.entries;
        if (height == 0) {
            for (int index = 0; index < node.childCount; index++) {
                if (predicate.test(child[index].key, key)) {
                    consumer.accept(child[index].key, child[index].value);
                }
            }
        } else {
            for (int index = 0; index < node.childCount; index++) {
                if (predicate.test(node.entries[index].key, key)) {
                    _search(key, node.entries[index].next, height - 1, consumer, predicate);
                }
            }
        }

    }

    private final BiPredicate<K, K> lte = (k1, k2) -> k1.compareTo(k2) <= 0;
    private final BiPredicate<K, K> gte = (k1, k2) -> k1.compareTo(k2) >= 0;

    private static class Node<K extends Comparable<K>, V> {
        private int childCount;
        private final NodeEntry<K, V>[] entries = (NodeEntry<K, V>[]) new NodeEntry[MAX_CHILDREN];

        public Node(int size) {
            this.childCount = size;
        }

        public boolean isLast(int element) {
            return element == childCount;
        }
    }

    private static class NodeEntry<K extends Comparable<K>, V> {
        private K key;
        private V value;
        private Node<K, V> next;

        private NodeEntry(K key, V value, Node<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }

        @Override
        public String toString() {
            return String.format("%s(%s)", this.getClass().getSimpleName(), this.key);
        }
    }

    public int size() {
        return size;
    }

    public void forEach(BiConsumer<K, V> consumer) {
        forEach(root, height, consumer);
    }


    private String forEach(Node<K, V> h, int ht, BiConsumer<K, V> consumer) {
        StringBuilder s = new StringBuilder();
        NodeEntry<K, V>[] children = h.entries;
        if (ht == 0) {
            for (int j = 0; j < h.childCount; j++) {
                consumer.accept(children[j].key, children[j].value);
            }
        } else {
            for (int j = 0; j < h.childCount; j++) {
                forEach(children[j].next, ht - 1, consumer);
            }
        }
        return s.toString();
    }
}
