package corejavasamples.jdk12.gc;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.CompletableFuture.runAsync;

/*
JEP 318: Epsilon: A No-Op Garbage Collector
    Reference - https://openjdk.java.net/jeps/318

    -XX:+UnlockExperimentalVMOptions -XX:+UseEpsilonGC -Xlog:gc -Xmx1g -Dmb=2000

 */
public class MultiThreadMemoryAllocator {

    public static final int KB = 1024;
    static int mbToAllocate = Integer.getInteger("mb", 1000);

    public static void main(String[] args) throws InterruptedException {
        System.out.println(String.format("Start allocation of %s MBs", mbToAllocate));

        var counter = new AtomicInteger(0);
        var noOfThreads = Runtime.getRuntime().availableProcessors();
        var latch = new CountDownLatch(noOfThreads);

        for (var x = 0; x < noOfThreads; x++) {
            runAsync(() -> allocate(counter, latch));
        }

        latch.await();
        System.out.println("I was Alive after allocation");
    }

    private static void allocate(AtomicInteger counter, CountDownLatch latch) {
        while (true) {

            if (counter.get() > mbToAllocate) {
                latch.countDown();
                break;
            }

            var b = new byte[KB * KB];
            counter.incrementAndGet();
        }
    }
}