package query.page.allocator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UncheckedIOException;
import java.nio.file.Path;

public class SafeIO {

    public static void createNewFile(Path dataLocation) {
        try {
            dataLocation.toFile().createNewFile();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static RandomAccessFile open(Path location) {
        try {
            return new RandomAccessFile(location.toFile().getAbsolutePath(), "rw");
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void write(RandomAccessFile raf, byte[] data) {
        try {
            raf.write(data);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void write(RandomAccessFile raf, long position, byte[] data) {
        try {
            raf.seek(position);
            raf.write(data);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void commit(RandomAccessFile raf) {
        try {
            raf.getFD().sync();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }


    public static int read(RandomAccessFile raf, long position, byte[] data) {
        try {
            raf.seek(position);
            return raf.read(data);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
