package encoding.buffer;

import encoding.record.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/*
 File header
 Size(4B)MaxMessageID(8B)
 MessageID(8B)MessageSize(4B)Message(X Bytes)
 */

public class PersistentBuffer<T> implements RecordContainer<T>, Closeable {
    private final Path path;
    private final String formatName;
    private int HEADER_SIZE = MessageHeader.MESSAGE_ID + MessageHeader.MESSAGE_SIZE;

    private final AtomicLong messageId = new AtomicLong();

    private RandomAccessFile raf;
    private ByteBuffer buffer;

    private final RecordEncoder<T> writer;
    private final RecordDecoder<T> reader;
    private final AtomicInteger writeOffset = new AtomicInteger();
    private final int capacity;

    public PersistentBuffer(String formatName, Path file, int capacity, RecordEncoder<T> writer, RecordDecoder<T> reader) throws Exception {
        check(capacity, writer, reader);
        this.formatName = formatName;
        this.path = file;
        this.capacity = capacity;
        this.writer = writer;
        this.reader = reader;
        setupBuffer(capacity, file);
        System.out.println(String.format("Message Id %s, Total Bytes written %s ", messageId.get(), writeOffset.get()));
    }

    private void setupBuffer(int capacity, Path file) throws IOException {
        if (file.toFile().exists()) {
            mapBuffer(capacity, file);
            writeOffset.set(readLastWriteOffset());
            messageId.set(readMessageId());
        } else {
            mapBuffer(capacity, file);
            setFileHeader(0, 0);
            writeOffset.set(MessageHeader.MESSAGE_SIZE + MessageHeader.MESSAGE_ID);
        }
    }

    private long readMessageId() {
        return this.buffer.getLong(4);
    }

    private int readLastWriteOffset() {
        return this.buffer.getInt(0);
    }

    private void mapBuffer(int capacity, Path file) throws IOException {
        this.raf = openFile(file);
        this.buffer = allocateBuffer(capacity);
    }

    private MappedByteBuffer allocateBuffer(int capacity) throws IOException {
        return raf.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, capacity);
    }

    private RandomAccessFile openFile(Path file) throws FileNotFoundException {
        return new RandomAccessFile(file.toFile(), "rw");
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
        boolean allocated = allocate(localOffset, newSize, id);
        if (!allocated || writeOffset.get() > capacity)
            return false;

        int writeOffset = localOffset;
        /*
        Message [MessageId MessageLength Message]
        MessageId - 8 Bytes
        MessageLength - 4 Bytes
        Message - X bytes
         */
        setBytes(HEADER_SIZE + writeOffset, message); // Message
        writeOffset = setLong(writeOffset, id); // Message Id
        setInt(writeOffset, message.length); // Message Size
        return true;
    }

    private int messageLength(byte[] message) {
        return HEADER_SIZE + message.length;
    }

    private boolean noSpace(int currentCapacity) {
        return currentCapacity > capacity;
    }

    private boolean allocate(int currentOffset, int allocationSize, long id) {
        boolean r = this.writeOffset.compareAndSet(currentOffset, allocationSize);
        if (r) {
            setFileHeader(allocationSize, id);
        }
        return r;
    }

    private void setFileHeader(int allocationSize, long id) {
        buffer.putInt(0, allocationSize);
        buffer.putLong(4, id);
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
        int start = MessageHeader.MESSAGE_SIZE + MessageHeader.MESSAGE_ID;
        readMessages(consumer, start, writeOffset.get());
    }

    @Override
    public void read(long offSet, RecordConsumer<T> consumer) {
        readMessages(consumer, (int) offSet, writeOffset.get());
    }

    private void readMessages(RecordConsumer<T> consumer, int start, int end) {
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
    public boolean write(T message) {
        byte[] bytes = writer.toBytes(message);
        return writeMessage(bytes);
    }

    @Override
    public void close() {
        try {
            this.raf.getChannel().force(true);
            this.raf.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public int size() {
        return (int) readMessageId();
    }

    @Override
    public String formatName() {
        return formatName;
    }

}
