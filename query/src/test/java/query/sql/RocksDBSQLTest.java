package query.sql;

import org.junit.jupiter.api.BeforeEach;
import query.kv.KeyValueStore;
import query.kv.memory.InMemoryStore;
import query.kv.persistent.rocks.RocksStore;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class RocksDBSQLTest extends SimpleSQLContractTest {
    @BeforeEach
    public void createDB() {
        this.db = rocks();
    }


    public KeyValueStore rocks() {
        File tmpdir = new File(System.getProperty("java.io.tmpdir"), "rocks-simple-sql");
        System.out.println("DB created at " + tmpdir.getAbsolutePath());
        cleanFiles(tmpdir);
        return new RocksStore(tmpdir);
    }

    private void cleanFiles(File tmpdir) {
        try {
            Files.list(tmpdir.toPath()).forEach(f -> {
                try {
                    Files.deleteIfExists(f);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
