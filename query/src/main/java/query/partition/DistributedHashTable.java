package query.partition;

public class DistributedHashTable {
    private final DistributedHash<Node> hash;

    public DistributedHashTable(DistributedHash<Node> hash) {
        this.hash = hash;
    }

    public void put(Object key, Object value) {
        findSlot(key).put(key, value);
    }

    private Node findSlot(Object key) {
        return hash.findSlot(key);
    }

    public Object get(Object key) {
        return findSlot(key).get(key);
    }
}
