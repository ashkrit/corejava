package encoding.record;

import java.io.Closeable;

public interface RecordContainer<T> extends Closeable {
    boolean append(T message);

    void read(RecordConsumer<T> reader);

    void read(long offSet, RecordConsumer<T> reader);

    default void close() {
    }

    int size();

    String formatName();

}
