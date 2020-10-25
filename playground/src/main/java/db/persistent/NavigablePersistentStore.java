package db.persistent;

import java.util.function.Consumer;
import java.util.function.Function;

public interface NavigablePersistentStore {
    void put(byte[] key, byte[] value);

    byte[] get(byte[] key);

    <Row_Type> void iterate(String fromKey, Function<byte[], Row_Type> converter, Consumer<Row_Type> consumer, int limit);

    <Row_Type> void iterate(String fromKey, String toKey, Function<byte[], Row_Type> converter, Consumer<Row_Type> consumer, int limit);
}
