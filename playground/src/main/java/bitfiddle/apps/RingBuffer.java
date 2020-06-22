package bitfiddle.apps;

public class RingBuffer<T> {

    private final int capacity;
    private final Object[] buffer;
    private final int mask;
    private int read = 0;
    private int write = 0;

    public RingBuffer(int size) {
        this.capacity = powOf2(size);
        this.mask = capacity - 1;
        buffer = new Object[this.capacity];
    }

    public int capacity() {
        return capacity;
    }

    public boolean write(T value) {
        if (buffer[offset(write)] != null)
            return false;
        buffer[offset(write++)] = value;
        return true;
    }


    public T read() {
        if (read == write)
            return null;
        T value = (T) buffer[offset(read)];
        buffer[offset(read++)] = null;
        return value;

    }

    public int size() {
        return write - read;
    }

    private int offset(int index) {
        return index & mask;
        //return index % capacity;
    }

    private int powOf2(int size) {
        int capacity = 1;
        while (capacity < size) {
            capacity <<= 1;
        }
        return capacity;
    }
}
