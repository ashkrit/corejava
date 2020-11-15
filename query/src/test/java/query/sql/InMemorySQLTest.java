package query.sql;

import org.junit.jupiter.api.BeforeEach;
import query.kv.memory.InMemoryStore;

public class InMemorySQLTest extends SimpleSQLContractTest {
    @BeforeEach
    public void createDB() {
        this.db = new InMemoryStore();
    }
}
