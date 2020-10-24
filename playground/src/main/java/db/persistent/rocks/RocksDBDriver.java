package db.persistent.rocks;


import org.rocksdb.Options;
import org.rocksdb.RocksDB;

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
}
