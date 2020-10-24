package db.rocks;

import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;

import java.util.function.Consumer;
import java.util.function.Function;

public class NavigableRocks {
    private final RocksDB db;

    public NavigableRocks(RocksDB db) {
        this.db = db;
    }

    public void put(byte[] key, byte[] value) {
        try {
            db.put(key, value);
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] get(byte[] key) {
        try {
            return db.get(key);
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        }
    }

    public <Row_Type> void iterate(String fromKey, Function<byte[], Row_Type> converter, Consumer<Row_Type> consumer, int limit) {
        try (RocksIterator itr = db.newIterator()) {
            itr.seek(fromKey.getBytes());
            int tracker = limit;
            while (itr.isValid() && tracker > 0) {
                String s = new String(itr.key());
                if (!s.startsWith(fromKey)) {
                    break;
                }
                consumer.accept(converter.apply(itr.value()));
                itr.next();
                tracker--;
            }
        }
    }
}
