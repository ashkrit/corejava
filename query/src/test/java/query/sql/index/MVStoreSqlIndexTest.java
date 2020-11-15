package query.sql.index;

import org.junit.jupiter.api.BeforeEach;
import query.kv.KeyValueStore;
import query.kv.persistent.mvstore.H2MVStore;
import query.sql.SimpleSQLContractTest;

import java.io.File;

public class MVStoreSqlIndexTest extends SQLIndexContractTest {
    @BeforeEach
    public void createDB() {
        this.db = mvStore();
    }

    public KeyValueStore mvStore() {
        File tmpdir = new File(new File(System.getProperty("java.io.tmpdir"), "mvstore"), "h2mv-index-sql");
        System.out.println("DB created at " + tmpdir.getAbsolutePath());
        tmpdir.getParentFile().mkdirs();
        if (tmpdir.exists()) {
            tmpdir.delete();
        }
        return new H2MVStore(tmpdir);
    }
}
