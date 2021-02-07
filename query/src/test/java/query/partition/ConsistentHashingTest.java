package query.partition;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConsistentHashingTest {

    @Test
    public void add_nodes() {
        HashFunction f = Hashing.murmur3_32();
        ConsistentHashing<Node> hashing = new ConsistentHashing<>(v -> f.hashBytes(v).asInt(), 3, n -> n.name);

        Map<String, Node> nodes = new HashMap<String, Node>() {{
            put("NodeA", new Node("NodeA"));
            put("NodeB", new Node("NodeB"));
            put("NodeC", new Node("NodeC"));
        }};


        nodes.entrySet().stream().map(Map.Entry::getValue).forEach(hashing::add);

        Map<Node, Long> nodeMappings = hashing.nodes();

        assertAll(
                () -> assertEquals(3, nodeMappings.get(nodes.get("NodeA"))),
                () -> assertEquals(3, nodeMappings.get(nodes.get("NodeB"))),
                () -> assertEquals(3, nodeMappings.get(nodes.get("NodeC")))
        );

    }
}
