package query.kv.persistent;

import query.kv.KeyValueStoreUpdateContractTest;
import query.kv.persistent.mvstore.H2MVStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;

public class H2MVStoreUpdateTest extends KeyValueStoreUpdateContractTest {

    @BeforeEach
    public void createDB() {
        File tmpdir = new File(new File(System.getProperty("java.io.tmpdir"), "mvstore"), "h2mv");
        System.out.println("DB created at " + tmpdir.getAbsolutePath());
        tmpdir.getParentFile().mkdirs();
        if (tmpdir.exists()) {
            tmpdir.delete();
        }
        this.db = new H2MVStore(tmpdir);
    }

    @AfterEach
    public void cleanDB() {
        ((H2MVStore) this.db).close();
    }


}
