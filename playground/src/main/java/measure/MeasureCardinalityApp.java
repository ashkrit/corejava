package measure;

import org.HdrHistogram.Histogram;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static java.util.stream.IntStream.range;

public class MeasureCardinalityApp {

    public static void main(String[] args) {

        long time = System.nanoTime();

        Histogram histogram = new Histogram(2);
        range(0, 1000_000 * 100)
                .forEach(v -> {
                    int merchant = merchant();
                    histogram.recordValue(merchant);
                });
        histogram.outputPercentileDistribution(System.out, 1.0);

        long total = System.nanoTime() - time;

        System.out.println(String.format("Time taken %s , Estimated Size %s KB", TimeUnit.NANOSECONDS.toMillis(total), histogram.getNeededByteBufferCapacity() / 1024));
    }

    private static int merchant() {
        int value = ThreadLocalRandom.current().nextInt(100_000);
        return value % 2 == 0 ? 101 : value;
    }
}
