package query.partition;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConsistentHashingTest {


    Map<String, Node> nodes = new HashMap<String, Node>() {{
        put("NodeA", new Node("NodeA"));
        put("NodeB", new Node("NodeB"));
        put("NodeC", new Node("NodeC"));
    }};


    @Test
    public void add_nodes() {
        HashFunction f = Hashing.murmur3_32();
        ConsistentHashing<Node> hashing = new ConsistentHashing<>(v -> f.hashBytes(v).asInt(), 3, n -> n.name);


        nodes.entrySet().stream().map(Map.Entry::getValue).forEach(hashing::add);

        Map<Node, Long> nodeMappings = hashing.nodes();

        assertAll(
                () -> assertEquals(3, nodeMappings.get(nodes.get("NodeA"))),
                () -> assertEquals(3, nodeMappings.get(nodes.get("NodeB"))),
                () -> assertEquals(3, nodeMappings.get(nodes.get("NodeC")))
        );

    }


    @Test
    public void insert_and_get_key() {

        DistributedHashTable hashTable = new DistributedHashTable(configNodes());
        hashTable.put("key1", "value1");

        assertEquals("value1", hashTable.get("key1"));

    }

    @NotNull
    public ConsistentHashing<Node> configNodes() {
        HashFunction f = Hashing.murmur3_32();
        ConsistentHashing<Node> hashing = new ConsistentHashing<>(v -> f.hashBytes(v).asInt(), 3, n -> n.name);
        nodes.entrySet().stream().map(Map.Entry::getValue).forEach(hashing::add);
        return hashing;
    }
}
