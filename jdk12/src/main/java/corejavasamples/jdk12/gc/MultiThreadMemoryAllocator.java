package corejavasamples.jdk12.gc;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

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

        AtomicInteger counter = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(Runtime.getRuntime().availableProcessors());
        for (int x = 0; x < Runtime.getRuntime().availableProcessors(); x++) {
            CompletableFuture.runAsync(() -> {
                while (true) {
                    if (counter.get() > mbToAllocate) {
                        latch.countDown();
                        break;
                    }
                    byte[] b = new byte[KB * KB];
                    counter.incrementAndGet();
                }
            });
        }

        latch.await();
        System.out.println("I was Alive after allocation");
    }
}