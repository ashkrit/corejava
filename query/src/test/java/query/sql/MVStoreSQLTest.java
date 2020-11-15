package query.sql;

import org.junit.jupiter.api.BeforeEach;
import query.kv.KeyValueStore;
import query.kv.memory.InMemoryStore;
import query.kv.persistent.mvstore.H2MVStore;

import java.io.File;

public class MVStoreSQLTest extends SimpleSQLContractTest {
    @BeforeEach
    public void createDB() {
        this.db = mvStore();
    }

    public KeyValueStore mvStore() {
        File tmpdir = new File(new File(System.getProperty("java.io.tmpdir"), "mvstore"), "h2mv");
        System.out.println("DB created at " + tmpdir.getAbsolutePath());
        tmpdir.getParentFile().mkdirs();
        if (tmpdir.exists()) {
            tmpdir.delete();
        }
        return new H2MVStore(tmpdir);
    }
}
