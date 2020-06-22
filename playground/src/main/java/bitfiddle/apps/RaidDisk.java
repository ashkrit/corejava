package bitfiddle.apps;

import bitfiddle.MoreInts;

public class RaidDisk {

    private final byte[] bytes;

    public RaidDisk(int capacity) {
        this.bytes = new byte[capacity];
    }

    public int capacity() {
        return bytes.length;
    }

    public void set(int offSet, byte b) {
        this.bytes[offSet] = b;
    }

    public String toBinary() {
        StringBuffer sb = new StringBuffer();
        for (byte b : bytes) {
            if (b != 0) {
                sb.append(MoreInts.toBinary(b));
            }
        }
        return sb.toString();
    }

    public RaidDisk xor(RaidDisk otherDisk) {
        if (this.capacity() != otherDisk.capacity()) {
            //throw new RuntimeException("Unable to merge disk with different capacity");
        }
        int newCapacity = Math.max(this.capacity(), otherDisk.capacity());
        int minCapacity = Math.min(this.capacity(), otherDisk.capacity());
        RaidDisk newDisk = new RaidDisk(newCapacity);


        for (int index = 0; index < minCapacity; index++) {
            byte value = (byte) (this.bytes[index] ^ otherDisk.bytes[index]);
            newDisk.set(index, value);
        }

        RaidDisk bigDisk = this.capacity() > otherDisk.capacity() ? this : otherDisk;
        for (int index = minCapacity; minCapacity < newCapacity; minCapacity++) {
            newDisk.set(index, bigDisk.bytes[index]);
        }
        return newDisk;
    }
}
