package encoding.record;


public interface RecordConsumer<T> {
    boolean onNext(long offset, int size, long messageId, T message);
}