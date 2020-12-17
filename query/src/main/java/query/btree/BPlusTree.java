package query.btree;

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

        Node<K, V> newNode = new Node<>(2);
        newNode.entries[0] = new NodeEntry<>(root.entries[0].key, null, root);
        newNode.entries[1] = new NodeEntry<>(n.entries[0].key, null, n);
        root = newNode;
        height++;
    }

    private Node<K, V> insert(Node<K, V> r, K key, V value, int height) {
        int x = 0;
        NodeEntry<K, V> t = new NodeEntry<>(key, value, null);
        if (height == 0) {
            for (x = 0; x < r.childCount; x++) {
                if (lessThan(key, r.entries[x].key)) {
                    break;
                }
            }
        } else {
            for (x = 0; x < r.childCount; x++) {
                int nextElement = x + 1;
                if (r.isLast(nextElement) || lessThan(key, r.entries[nextElement].key)) {

                    Node<K, V> c = insert(r.entries[x++].next, key, value, height - 1);

                    if (c == null) return null;
                    t.key = c.entries[0].key;
                    t.value = null;
                    t.next = c;
                    break;
                }
            }
        }

        for (int i = r.childCount; i > x; i--) {
            r.entries[i] = r.entries[i - 1];
        }
        r.entries[x] = t;
        r.childCount++;

        if (r.childCount < MAX_CHILDREN) {
            return null;
        } else {
            return split(r);
        }
    }

    private Node<K, V> split(Node<K, V> r) {
        int newChildCount = MAX_CHILDREN / 2;
        Node<K, V> t = new Node<>(newChildCount);
        r.childCount = newChildCount;
        for (int i = 0; i < newChildCount; i++) {
            t.entries[i] = r.entries[newChildCount + i];
        }
        return t;
    }

    public boolean lessThan(K key, K key1) {
        return key.compareTo(key1) < 0;
    }

    public V get(K key) {
        return search(root, key, height);
    }

    private V search(Node<K, V> root, K key, int height) {
        NodeEntry<K, V>[] child = root.entries;
        if (height == 0) {
            for (int index = 0; index < root.childCount; index++) {
                if (key.compareTo(child[index].key) == 0) {
                    return child[index].value;
                }
            }
        } else {
            for (int index = 0; index < root.childCount; index++) {
                if (root.isLast(index + 1) || lessThan(key, root.entries[index + 1].key)) {
                    return (V) search(root.entries[index].next, key, height - 1);
                }
            }
        }
        return null;
    }

    private static class Node<K extends Comparable<K>, V> {
        private int childCount;
        private final NodeEntry<K, V>[] entries = new NodeEntry[MAX_CHILDREN];

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
        private Node next;

        private NodeEntry(K key, V value, Node next) {
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
}
