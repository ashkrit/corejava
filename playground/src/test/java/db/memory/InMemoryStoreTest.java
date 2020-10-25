package db.memory;


import db.KeyValueStoreContractTest;
import org.junit.jupiter.api.BeforeEach;

public class InMemoryStoreTest extends KeyValueStoreContractTest {

    @BeforeEach
    public void createDB() {
        this.db = new InMemoryStore();
    }

}