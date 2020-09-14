package stream.parallel;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingDouble;

public class MonteCarloSimulationTest {

    ThreadLocalRandom random = ThreadLocalRandom.current();

    @Test
    public void simulate() {

        int simulation = 20000;
        double fraction = 1.0D / simulation;


        Map<Integer, Double> values = IntStream.range(0, simulation)
                .parallel()
                .mapToObj($ -> throwDice())
                .collect(groupingBy(side -> side, summingDouble($ -> fraction)));

        System.out.println(values);

    }

    private int throwDice() {
        return random.nextInt(1, 6)
                + random.nextInt(1, 6);
    }

}
