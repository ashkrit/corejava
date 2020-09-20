package stream.ch006.morecollectors;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static java.util.Arrays.parallelPrefix;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SlidingWindowTest {

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
