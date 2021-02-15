package query.partition;

import com.google.common.hash.Funnel;
import com.google.common.hash.Hashing;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static query.partition.HRWHashing.defaultHash;

public class HRWHashingTest {


    Map<String, Node> nodes = new HashMap<String, Node>() {{
        put("NodeA", new Node("NodeA"));
        put("NodeB", new Node("NodeB"));
        put("NodeC", new Node("NodeC"));
    }};


    @Test
    public void add_nodes() {

        HRWHashing<Node> hashing = new HRWHashing<>((n, k) -> 1L);

        nodes.entrySet()
                .stream()
                .map(Map.Entry::getValue).forEach(hashing::add);

        Map<Node, Long> nodeMappings = hashing.nodes();

        assertAll(
                () -> assertEquals(1, nodeMappings.get(nodes.get("NodeA"))),
                () -> assertEquals(1, nodeMappings.get(nodes.get("NodeB"))),
                () -> assertEquals(1, nodeMappings.get(nodes.get("NodeC")))
        );

    }


    @Test
    public void find_node_with_max_score() {

        Map<String, Long> hash = new HashMap<String, Long>() {{
            put("key1_NodeA", Long.MAX_VALUE);
            put("key2_NodeB", Long.MAX_VALUE);
        }};

        HRWHashing<Node> hashing = new HRWHashing<>((n, k) -> {
            String key = String.format("%s_%s", k.toString(), n.name);
            return hash.getOrDefault(key, Long.valueOf(key.hashCode()));
        });

        nodes.entrySet()
                .stream()
                .map(Map.Entry::getValue).forEach(hashing::add);

        assertAll(
                () -> assertEquals("NodeA", hashing.findSlot("key1").name),
                () -> assertEquals("NodeB", hashing.findSlot("key2").name)
        );

    }


    @Test
    public void put_and_get_value() {
        Funnel<Node> nodeFunnel = (from, into) -> into.putBytes(from.name.getBytes());
        Funnel<Object> keyFunnel = (from, into) -> into.putBytes(from.toString().getBytes());
        HRWHashing hashing = new HRWHashing(defaultHash(nodeFunnel, keyFunnel, Hashing.murmur3_128()));

        DistributedHashTable hashTable = new DistributedHashTable(hashing);

        nodes.entrySet()
                .stream()
                .map(Map.Entry::getValue)
                .forEach(hashing::add);

        hashTable.put("key1", "value1");

        assertEquals("value1", hashTable.get("key1"));

    }
}
