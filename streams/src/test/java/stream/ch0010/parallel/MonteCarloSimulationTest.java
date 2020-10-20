package stream.ch0010.parallel;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingDouble;

@DisplayName("Simple Parallel streams")
public class MonteCarloSimulationTest {

    ThreadLocalRandom random = ThreadLocalRandom.current();
    int simulation = 20000;
    double fraction = 1.0D / simulation;

    @Test
    public void simulate_in_neat_way() {

        Map<Integer, Double> values = IntStream.range(0, simulation)
                .parallel()
                .mapToObj($ -> throwDice())
                .collect(groupingBy(side -> side, summingDouble($ -> fraction)));

        System.out.println(values);

    }

    @Test
    public void simulate_in_hard_way() {

        Map<Integer, Double> supplier = new HashMap<>();
        BiFunction<Map<Integer, Double>, Integer, Map<Integer, Double>> accumulator = (container, value) -> {
            container.put(value, fraction);
            return container;
        };

        BinaryOperator<Map<Integer, Double>> combiner = (m1, m2) -> {
            m2.forEach((k, v) -> m1.merge(k, v, (v1, v2) -> v1 + v2));
            return m1;
        };


        Map<Integer, Double> values = IntStream.range(0, simulation)
                .parallel()
                .mapToObj($ -> throwDice())
                .reduce(supplier, accumulator, combiner);

        System.out.println(values);

    }

    private int throwDice() {
        return random.nextInt(1, 6)
                + random.nextInt(1, 6);
    }

}
