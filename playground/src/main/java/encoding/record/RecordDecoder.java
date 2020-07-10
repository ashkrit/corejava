package encoding.record;


public interface RecordDecoder<T> {
    T toRecord(byte[] bytes);
}