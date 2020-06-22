package bitfiddle.apps;

import bitfiddle.MoreInts;

/*
    Represents virtual Disk with X capacity. If system contains N disk then N copies are required to get full fault tolerance.
    Using 0XR - for every pair of Disk we can just keep only copy using XOR and it is possible to restore any of the failed disk using XOR copy.
    This reduces disk requirement by 50%.
 */

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
