package query.partition;

import com.google.common.hash.*;
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

        Funnel<Node> nodeFunnel = (from, into) -> {
        };

        Funnel<Object> keyFunnel = (from, into) -> {
        };

        HRWHashing<Node, Object> hashing = new HRWHashing<>(nodeFunnel, keyFunnel);

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
}
