package db.memory;


import db.KeyValueStoreContractTest;
import db.memory.InMemoryStore;
import org.junit.jupiter.api.BeforeEach;

public class InMemoryStoreTest extends KeyValueStoreContractTest {

    @BeforeEach
    public void createDB() {
        this.db = new InMemoryStore();
    }

}
