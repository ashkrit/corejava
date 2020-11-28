package query.page.read;

import query.page.PageOffSets;

import java.nio.ByteBuffer;


public class RecordReader {
    private final int pointerSize;
    private final int readDataSize;

    public RecordReader(int pointerSize, int readDataSize) {
        this.pointerSize = pointerSize;
        this.readDataSize = readDataSize;
    }

    public int read(byte[] writeBuffer, int recordIndex, ByteBuffer readBuffer) {
        int slotIndex = slotIndex(recordIndex);
        int startPosition = startPosition(recordIndex, slotIndex, readBuffer);
        int recordSize = recordSize(readBuffer, slotIndex, startPosition); // Bytes to read from current position
        return readTuple(writeBuffer, startPosition, recordSize, readBuffer);
    }

    private int startPosition(int record, int slotIndex, ByteBuffer readBuffer) {
        if (record == 0) {
            return PageOffSets.DATA_OFFSET;
        } else {
            return readBuffer.getInt(slotIndex + pointerSize);
        }
    }

    private int readTuple(byte[] writeBuffer, int startPos, int bytesToRead, ByteBuffer readBuffer) {
        for (int start = 0; start < bytesToRead; start++) {
            writeBuffer[start] = readBuffer.get(startPos + start);
        }
        return bytesToRead;
    }

    private static int recordSize(ByteBuffer readBuffer, int slotIndex, int startPosition) {
        return readBuffer.getInt(slotIndex) - startPosition;
    }

    private int slotIndex(int record) {
        return (readDataSize - (record * pointerSize)) - pointerSize;
    }
}
