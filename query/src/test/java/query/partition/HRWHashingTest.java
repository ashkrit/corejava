package query.partition;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
    public void insert_and_get_key() {

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
}
