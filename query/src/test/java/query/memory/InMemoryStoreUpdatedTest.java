package query.memory;


import query.KeyValueStoreUpdateContractTest;
import org.junit.jupiter.api.BeforeEach;

public class InMemoryStoreUpdatedTest extends KeyValueStoreUpdateContractTest {

    @BeforeEach
    public void createDB() {
        this.db = new InMemoryStore();
    }

}
