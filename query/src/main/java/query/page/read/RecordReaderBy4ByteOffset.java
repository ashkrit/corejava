package query.page.read;

import java.nio.ByteBuffer;


public class RecordReaderBy4ByteOffset {

    public static final int POINTER_SIZE = 4;
    private final int readBufferSize;
    private final ByteBuffer readBuffer;
    private final int startOffset;

    public RecordReaderBy4ByteOffset(int startOffset, ByteBuffer readBuffer, int readBufferSize) {
        this.startOffset = startOffset;
        this.readBufferSize = readBufferSize;
        this.readBuffer = readBuffer;
    }

    public int read(byte[] writeBuffer, int recordIndex) {
        int slotIndex = slotIndex(recordIndex);
        int startPosition = startPosition(recordIndex, slotIndex);
        int recordSize = recordSize(slotIndex, startPosition); // Bytes to read from current position
        return readTuple(writeBuffer, startPosition, recordSize, readBuffer);
    }

    private int readTuple(byte[] writeBuffer, int startPos, int bytesToRead, ByteBuffer readBuffer) {
        for (int start = 0; start < bytesToRead; start++) {
            writeBuffer[start] = readBuffer.get(startPos + start);
        }
        return bytesToRead;
    }

    private int startPosition(int record, int slotIndex) {
        if (record == 0) {
            return startOffset;
        } else {
            return readBuffer.getInt(slotIndex + POINTER_SIZE);
        }
    }

    private int recordSize(int slotIndex, int startPosition) {
        return readBuffer.getInt(slotIndex) - startPosition;
    }

    private int slotIndex(int record) {
        return (readBufferSize - (record * POINTER_SIZE)) - POINTER_SIZE;
    }
}
