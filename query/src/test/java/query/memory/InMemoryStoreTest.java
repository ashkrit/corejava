package query.memory;


import query.KeyValueStoreContractTest;
import org.junit.jupiter.api.BeforeEach;

public class InMemoryStoreTest extends KeyValueStoreContractTest {

    @BeforeEach
    public void createDB() {
        this.db = new InMemoryStore();
    }

}
