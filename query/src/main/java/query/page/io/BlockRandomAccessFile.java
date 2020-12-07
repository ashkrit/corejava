package query.page.io;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UncheckedIOException;

public class BlockRandomAccessFile {
    private final RandomAccessFile raf;

    public BlockRandomAccessFile(RandomAccessFile raf) {
        this.raf = raf;
    }

    public void write(long position, byte[] data) {
        try {
            raf.seek(position);
            raf.write(data);
        } catch (IOException e) {
            throw asUnChecked(e);
        }
    }

    public UncheckedIOException asUnChecked(IOException e) {
        return new UncheckedIOException(e);
    }

    public void commit() {
        try {
            raf.getFD().sync();
        } catch (IOException e) {
            throw asUnChecked(e);
        }
    }


    public int read(long position, byte[] data) {
        try {
            raf.seek(position);
            return raf.read(data);
        } catch (IOException e) {
            throw asUnChecked(e);
        }
    }

}
