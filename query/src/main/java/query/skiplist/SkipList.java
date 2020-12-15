package query.skiplist;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SkipList<K extends Comparable, V> implements Iterable<SkipList.SkipNode<K, V>> {

    private static final int LEVEL_0 = 0;
    private final SkipNodeHead<K, V> head = new SkipNodeHead<>(null);

    private final BiConsumer<Integer, K> noOpLevelListener = (level, s) -> {
    };

    private final Consumer<K> noOpKeyListener = k -> {
    };

    public void put(K key, V value) {

        SkipNode<K, V> node = new SkipNode<>(key, value, LEVEL_0, null);
        SkipNode<K, V> headNode = head(node.level);
        if (headNode == null) {
            if (!headContainer(node.level).casHead(null, node)) {
                insertKey(node);
            }
        } else {
            insertKey(node);
        }
    }

    private SkipNode<K, V> insertKey(SkipNode<K, V> newNode) {

        //No of Skips
        SkipNode<K, V> skipNode = createSkipLevels(newNode);
        insertByLevel(skipNode);

        while (skipNode.up != null) {
            insertByLevel(skipNode.up);
            skipNode = skipNode.up;
        }
        return newNode;
    }

    private SkipNode<K, V> createSkipLevels(SkipNode<K, V> newNode) {
        SkipNode<K, V> current = newNode;
        SkipNode<K, V> downNode = newNode.down;
        while (ThreadLocalRandom.current().nextBoolean()) {
            int level = current.level + 1;
            head.nextLevel(level);

            SkipNode<K, V> nextLevelNode = new SkipNode<>(current.key, current.value, level, null);
            nextLevelNode.down = current;
            current.up = nextLevelNode;
            current.down = downNode;

            downNode = current;
            current = nextLevelNode;
        }

        return newNode;
    }

    private SkipNode<K, V> insertByLevel(SkipNode<K, V> newNode) {
        for (; ; ) {
            SkipNode<K, V> currentNode = head(newNode.level);
            SkipNode<K, V> previousNode = null;
            while (currentNode != null) {
                int matchValue = newNode.key.compareTo(currentNode.key);
                if (matchValue == 0) {
                    return null; //Key already exists
                } else if (matchValue > 0) {
                    previousNode = currentNode;
                    currentNode = currentNode.nextNode();
                } else {
                    newNode.casNext(null, currentNode);
                    if (isPreviousNull(previousNode)) {
                        if (headContainer(newNode.level).casHead(currentNode, newNode)) {
                            return newNode;
                        } else {
                            //CAS failed , try again
                            System.out.printf("Old %s, New %s", currentNode, newNode);
                            currentNode = head(newNode.level);
                            previousNode = null;
                        }
                    } else {
                        if (previousNode.casNext(currentNode, newNode)) {
                            return newNode;
                        } else {
                            //CAS failed , try again
                            System.out.printf("Previous -> Old %s, New %s", currentNode, newNode);
                            currentNode = head(newNode.level);
                            previousNode = null;
                            continue;
                        }
                    }
                }
            }
            if (previousNode.casNext(currentNode, newNode)) {
                //Insert level
                return newNode;
            }
        }
    }

    private boolean isPreviousNull(SkipNode<K, V> previousNode) {
        return previousNode == null;
    }

    private SkipNode<K, V> head(int level) {
        return head.levels.get(level).head.get();
    }

    private SkipNodeHead<K, V> headContainer(int level) {
        return head.levels.get(level);
    }

    @Override
    public Iterator<SkipNode<K, V>> iterator() {
        return levelIterator(LEVEL_0);
    }

    public Iterator<SkipNode<K, V>> iterator(int level) {
        return levelIterator(level);
    }

    @NotNull
    private Iterator<SkipNode<K, V>> levelIterator(int level) {
        return new Iterator<SkipNode<K, V>>() {
            AtomicReference<SkipNode<K, V>> node = headContainer(level).head;

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

    public V get(K key) {
        return get(key, noOpLevelListener, noOpKeyListener);
    }

    public V get(K key, BiConsumer<Integer, K> processor, Consumer<K> searchKey) {
        int levels = head.currentLevel;
        for (int level = levels; level >= 0; ) {
            Iterator<SkipNode<K, V>> itr = iterator(level);

            SkipNode<K, V> previous = itr.next();
            for (; previous != null; ) {
                processor.accept(previous.level, previous.key);

                level = previous.level;
                if (key.compareTo(previous.key) < 0) {
                    return null;// Not found
                } else if (previous.key.compareTo(key) == 0) {
                    return previous.value;
                }
                while (previous.nextNode() != null) {

                    SkipNode<K, V> current = previous.nextNode();
                    searchKey.accept(current.key);
                    int compareResult = key.compareTo(current.key);
                    if (compareResult == 0) {
                        return current.value;
                    } else if (compareResult < 0) {
                        break;
                    } else {
                        previous = current;
                    }
                }
                previous = previous.down;
                System.out.println();
            }
            level -= 1;
        }
        return null;
    }


    public static class SkipNode<K extends Comparable, V> {
        public final V value;
        public final K key;
        public final int level;
        public AtomicReference<SkipNode<K, V>> next;
        private SkipNode<K, V> down;
        private SkipNode<K, V> up;

        public SkipNode(K key, V value, int level, SkipNode<K, V> next) {
            this.key = key;
            this.value = value;
            this.level = level;
            this.next = new AtomicReference<>(next);
        }

        final boolean casNext(SkipNode<K, V> current, SkipNode<K, V> next) {
            boolean r = this.next.compareAndSet(current, next);
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
        public final ConcurrentMap<Integer, SkipNodeHead<K, V>> levels = new ConcurrentHashMap<>();
        public int currentLevel;

        public SkipNodeHead(SkipNode<K, V> head) {
            this.head = new AtomicReference<>(head);
            levels.put(LEVEL_0, this);
        }

        public void nextLevel(int level) {
            SkipNode<K, V> v = head.get();
            levels.putIfAbsent(level, new SkipNodeHead<>(new SkipNode<>(v.key, v.value, level, null)));
            synchronized (this) {
                this.currentLevel = Math.max(currentLevel, level);
            }
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

    public int level() {
        return head.currentLevel;
    }
}
