package measure;

import org.HdrHistogram.Histogram;

import java.io.Closeable;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.ThreadLocalRandom.current;
import static java.util.stream.IntStream.range;

public class MeasureLatencyApp {

    public static void main(String[] args) {
        long time = System.nanoTime();

        Histogram histogram = new Histogram(2);
        range(0, 1000_000)
                .forEach(v -> {
                    long timeTaken = doSomething(v);
                    histogram.recordValue(timeTaken);
                });
        histogram.outputPercentileDistribution(System.out, 1.0);

        long total = System.nanoTime() - time;

        System.out.println(String.format("Time taken %s , Estimated Size %s KB", TimeUnit.NANOSECONDS.toMillis(total), histogram.getNeededByteBufferCapacity() / 1024));

    }

    private static Closeable createRecorder(Histogram histogram) {
        return new Closeable() {
            long start = System.nanoTime();

            @Override
            public void close() {
                long diff = System.nanoTime() - start;
                histogram.recordValue(diff);
            }
        };
    }

    private static long doSomething(int $) {
        return current().nextLong(1, 1000 * 10);
    }
}
