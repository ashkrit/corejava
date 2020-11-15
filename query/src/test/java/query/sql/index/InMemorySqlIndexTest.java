package query.sql.index;

import org.junit.jupiter.api.BeforeEach;
import query.kv.memory.InMemoryStore;

public class InMemorySqlIndexTest extends SQLIndexContractTest {
    @BeforeEach
    public void createDB() {
        this.db = new InMemoryStore();
    }
}
