package encoding.buffer;

import encoding.record.*;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryBuffer<T> implements RecordContainer<T> {
    private int HEADER_SIZE = MessageHeader.MESSAGE_ID + MessageHeader.MESSAGE_SIZE;

    private final AtomicLong messageId = new AtomicLong();

    private final int capacity;
    private final ByteBuffer buffer;
    private final RecordEncoder<T> writer;
    private final RecordDecoder<T> reader;
    private final AtomicInteger writeOffset = new AtomicInteger();

    public InMemoryBuffer(int capacity, RecordEncoder<T> writer, RecordDecoder<T> reader) {
        check(capacity, writer, reader);
        this.capacity = capacity;
        this.buffer = ByteBuffer.allocate(capacity);
        this.writer = writer;
        this.reader = reader;
    }

    private void check(int capacity, RecordEncoder<T> writer, RecordDecoder<T> reader) {
        Objects.requireNonNull(writer, "writer is required");
        Objects.requireNonNull(reader, "reader is required");
        if (capacity < 1) {
            throw new IllegalArgumentException("Capacity must be > 0 ");
        }
    }

    private boolean writeMessage(byte[] message) {
        int localOffset = writeOffset.get();
        int allocationSize = messageLength(message);
        int newSize = localOffset + allocationSize;
        if (noSpace(newSize)) {
            throw new ArrayStoreException(String.format("Curr capacity %s but required is %s", capacity, newSize));
        }
        long id = messageId.incrementAndGet();
        boolean allocated = allocate(localOffset, newSize);
        if (!allocated || writeOffset.get() > capacity)
            return false;

        int writeOffset = localOffset;
        writeOffset = setLong(writeOffset, id);
        writeOffset = setInt(writeOffset, message.length);
        setBytes(writeOffset, message);

        return true;
    }

    private int messageLength(byte[] message) {
        return HEADER_SIZE + message.length;
    }

    private boolean noSpace(int currentCapacity) {
        return currentCapacity > capacity;
    }

    private boolean allocate(int currentOffset, int allocationSize) {
        return this.writeOffset.compareAndSet(currentOffset, allocationSize);
    }

    private int setLong(int offSet, long value) {
        buffer.putLong(offSet, value);
        return offSet + 8;
    }

    private int setInt(int offSet, int value) {
        buffer.putInt(offSet, value);
        return offSet + 4;
    }

    private int setBytes(int offSet, byte[] src) {
        int end = offSet + src.length;
        for (int i = offSet, v = 0; i < end; i++, v++)
            buffer.put(i, src[v]);
        return offSet + src.length;
    }

    private int readBytes(int offSet, byte[] src) {
        int end = offSet + src.length;
        for (int i = offSet, v = 0; i < end; i++, v++) {
            src[v] = buffer.get(i);
        }
        return offSet + src.length;
    }

    @Override
    public void read(RecordConsumer<T> consumer) {
        int start = 0;
        int end = writeOffset.get();
        boolean nextRequired = true;
        while (start < end && nextRequired) {
            long messageId = buffer.getLong(start);
            int messageSize = buffer.getInt(start + 8);
            byte[] message = new byte[messageSize];
            readBytes(start + MessageHeader.MESSAGE_ID + MessageHeader.MESSAGE_SIZE, message);
            nextRequired = consumer.onNext(start, messageSize, messageId, reader.toRecord(message));
            start += MessageHeader.MESSAGE_ID + MessageHeader.MESSAGE_SIZE + messageSize;
        }
    }

    @Override
    public void read(long offSet, RecordConsumer<T> reader) {

    }

    @Override
    public int size() {
        return messageId.intValue();
    }

    @Override
    public String formatName() {
        return "InMemory";
    }

    @Override
    public boolean append(T message) {
        byte[] bytes = writer.toBytes(message);
        return writeMessage(bytes);
    }

}
