package bitfiddle.apps;

import bitfiddle.MoreInts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RaidDiskTest {

    @Test
    public void restoreDisk() {

        RaidDisk disk1 = new RaidDisk(2);
        disk1.set(0, MoreInts.toByte("01101101"));
        disk1.set(1, MoreInts.toByte("00101101"));

        RaidDisk disk2 = new RaidDisk(1);
        disk2.set(0, MoreInts.toByte("11010100"));

        RaidDisk raidDisk = disk1.xor(disk2);

        RaidDisk newDisk1 = raidDisk.xor(disk2);
        RaidDisk newDisk2 = raidDisk.xor(disk1);

        assertEquals(disk1.toBinary(), newDisk1.toBinary());
        assertEquals(disk2.toBinary(), newDisk2.toBinary());
    }
}
