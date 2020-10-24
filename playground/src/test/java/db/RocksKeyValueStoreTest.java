package db;

import db.rocks.RocksKeyValueStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class RocksKeyValueStoreTest extends KeyValueDatabaseContractTest {

    @BeforeEach
    public void createDB() {
        File tmpdir = new File(System.getProperty("java.io.tmpdir"), "rocks");
        System.out.println("DB created at " + tmpdir.getAbsolutePath());
        cleanFiles(tmpdir);
        this.db = new RocksKeyValueStore(tmpdir);
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

    @AfterEach
    public void cleanDB() {
        ((RocksKeyValueStore) this.db).close();
    }


}
