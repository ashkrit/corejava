package bitfiddle.apps;

/*
    Mod(%) is very CPU intensive and current CPU has only 2 ports for doing ( % , /) operation but it has 5 ports for doing bitwise operation.
    In this example Mod(%) is computed using '&' operator by taking advantage of Pow of 2 capacity.
 */
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
