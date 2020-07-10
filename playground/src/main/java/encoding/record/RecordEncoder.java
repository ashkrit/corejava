package encoding.record;


public interface RecordEncoder<T> {
    byte[] toBytes(T message);
}