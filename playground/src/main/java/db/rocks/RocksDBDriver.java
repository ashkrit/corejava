package db.rocks;


import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

import java.io.File;
import java.io.IOException;

import static java.nio.file.Files.createDirectories;

public class RocksDBDriver {
    public static RocksDB openDatabase(File dbDir) {
        try {
            RocksDB.loadLibrary();
            Options options = createOptions(dbDir);
            return RocksDB.open(options, dbDir.getAbsolutePath());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Options createOptions(File dbDir) throws IOException {
        Options options = new Options();
        options.setCreateIfMissing(true);
        createDirectories(dbDir.getParentFile().toPath());
        createDirectories(dbDir.getAbsoluteFile().toPath());
        return options;
    }

    public static void put(RocksDB db, byte[] key, byte[] value) {
        try {
            db.put(key, value);
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] get(RocksDB db, byte[] key) {
        try {
            return db.get(key);
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        }
    }
}
