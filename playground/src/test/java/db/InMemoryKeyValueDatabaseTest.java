package db;


import db.memory.InMemoryKV;
import org.junit.jupiter.api.BeforeEach;

public class InMemoryKeyValueDatabaseTest extends KeyValueDatabaseContractTest {

    @BeforeEach
    public void createDB() {
        this.db = new InMemoryKV();
    }

}
