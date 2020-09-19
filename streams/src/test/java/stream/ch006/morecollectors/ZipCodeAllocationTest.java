package stream.ch006.morecollectors;

import org.junit.jupiter.api.Test;
import stream.collectors.ZipCodeCollector;
import stream.collectors.ZipCodeCollector.Store;
import stream.collectors.ZipCodeCollectorV2;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Arrays.parallelPrefix;
import static org.junit.jupiter.api.Assertions.*;
import static stream.collectors.ZipCodeCollector.Store.building;

public class ZipCodeAllocationTest {
    /*
         Rule
          Stores that are under 2 KM of each other part of same group, stores that are more than 2 KM are part of other group.
         */

    List<Store> stores = Arrays.asList(
            building("S0", 67),
            building("S1", 100),
            building("S2", 101),
            building("S3", 107),
            building("S4", 108),
            building("S5", 114),
            building("S6", 116),
            building("S7", 117));


    @Test
    public void generatePostalCode() {

        Stream<Store> sortedByDistance = stores
                .stream()
                .sorted(Comparator.comparingLong(x -> x.position));

        List<List<String>> q =
                sortedByDistance.collect(ZipCodeCollector.create())
                        .stream()
                        .map(x -> x.stream().map(b -> b.name).collect(Collectors.toList()))
                        .collect(Collectors.toList());

        int index = 0;
        assertIterableEquals(Arrays.asList("S0"), q.get(index++));
        assertIterableEquals(Arrays.asList("S1", "S2"), q.get(index++));
        assertIterableEquals(Arrays.asList("S3", "S4"), q.get(index++));
        assertIterableEquals(Arrays.asList("S5", "S6", "S7"), q.get(index++));
        //Once group is identified then Zip code can be assigned

    }


    @Test
    public void generatePostalCodeUsingOtherCollector() {

        Stream<Store> sortedByDistance = stores
                .stream()
                .sorted(Comparator.comparingLong(x -> x.position));

        List<List<String>> q =
                sortedByDistance.collect(ZipCodeCollectorV2.create())
                        .stream()
                        .map(x -> x.stream().map(b -> b.name).collect(Collectors.toList()))
                        .collect(Collectors.toList());

        int index = 0;
        assertIterableEquals(Arrays.asList("S0"), q.get(index++));
        assertIterableEquals(Arrays.asList("S1", "S2"), q.get(index++));
        assertIterableEquals(Arrays.asList("S3", "S4"), q.get(index++));
        assertIterableEquals(Arrays.asList("S5", "S6", "S7"), q.get(index++));
    }


    @Test
    public void delta_encoding() {
        int[] q = {1, 2, 3, 4, 5, 2, 4, 6, 8, 10};

        //Encode
        int[] w = new int[q.length];
        IntStream.range(0, q.length).parallel().forEach(index -> {
            if (index == 0) {
                w[index] = q[index];
            } else {
                w[index] = q[index] - q[index - 1];
            }
        });

        //Decode
        parallelPrefix(w, Integer::sum);
        assertArrayEquals(q, w);
    }

    @Test
    public void sliding_window_sum() {
        int[] portfolioValues = {0, 24, 100, 220, 300, 320, 350}; //1 Week
        System.out.println(Arrays.toString(portfolioValues));
        parallelPrefix(portfolioValues, Integer::sum);

        System.out.println(Arrays.toString(portfolioValues));

        assertEquals(1314, sum(portfolioValues, 0, 6)); //Total earning
        assertEquals(300, sum(portfolioValues, 3, 4)); // On 5th Day
        System.out.println(Arrays.toString(portfolioValues));

    }

    private int sum(int[] portfolioValues, int startIndex, int endIndex) {
        return portfolioValues[endIndex] - portfolioValues[startIndex];
    }

}
