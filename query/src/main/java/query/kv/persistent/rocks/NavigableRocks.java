package query.kv.persistent.rocks;

import query.kv.persistent.NavigablePersistentStore;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class NavigableRocks implements NavigablePersistentStore {
    private final RocksDB db;

    public NavigableRocks(RocksDB db) {
        this.db = db;
    }

    @Override
    public void put(byte[] key, byte[] value) {
        try {
            db.put(key, value);
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] get(byte[] key) {
        try {
            return db.get(key);
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <Row_Type> void iterate(String fromKey, Function<byte[], Row_Type> converter, Consumer<Row_Type> consumer, int limit) {

        Predicate<String> predicate = v1 -> v1.startsWith(fromKey);
        match(fromKey, predicate.negate(), converter, consumer, limit);

    }

    @Override
    public <Row_Type> void iterate(String fromKey, String toKey, Function<byte[], Row_Type> converter, Consumer<Row_Type> consumer, int limit) {

        Predicate<String> predicate = v1 -> v1.compareTo(toKey) > 0;
        match(fromKey, predicate, converter, consumer, limit);
    }

    private <Row_Type> void match(String fromKey, Predicate<String> predicate, Function<byte[], Row_Type> converter, Consumer<Row_Type> consumer, int limit) {

        try (RocksIterator itr = db.newIterator()) {
            itr.seek(fromKey.getBytes());
            int tracker = limit;
            for (; itr.isValid() && tracker > 0; itr.next(), tracker--) {

                String s = new String(itr.key());
                if (predicate.test(s)) {
                    break;
                }
                consumer.accept(converter.apply(itr.value()));

            }
        }
    }
}
