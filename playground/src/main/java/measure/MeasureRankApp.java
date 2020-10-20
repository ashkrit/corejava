package measure;

import org.HdrHistogram.Histogram;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static java.util.stream.IntStream.range;

public class MeasureRankApp {

    public static void main(String[] args) {

        long time = System.nanoTime();

        Histogram histogram = new Histogram(2);
        range(0, 1000_000 * 100)
                .forEach(v -> {
                    long spend = spendValue();
                    histogram.recordValue(spend);
                });
        histogram.outputPercentileDistribution(System.out, 1.0);

        long total = System.nanoTime() - time;

        System.out.println(String.format("Time taken %s , Estimated Size %s KB", TimeUnit.NANOSECONDS.toMillis(total), histogram.getNeededByteBufferCapacity() / 1024));
    }

    private static long spendValue() {
        return (long) (new Random().nextDouble() * 20000d);
    }
}
