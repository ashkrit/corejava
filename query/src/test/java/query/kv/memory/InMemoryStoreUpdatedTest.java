package query.kv.memory;


import query.kv.KeyValueStoreUpdateContractTest;
import org.junit.jupiter.api.BeforeEach;

public class InMemoryStoreUpdatedTest extends KeyValueStoreUpdateContractTest {

    @BeforeEach
    public void createDB() {
        this.db = new InMemoryStore();
    }

}
