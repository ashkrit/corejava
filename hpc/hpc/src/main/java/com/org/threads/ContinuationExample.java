package com.org.threads;

import jdk.internal.vm.Continuation;
import jdk.internal.vm.ContinuationScope;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Add VM Options
 * <p>
 * --add-exports java.base/jdk.internal.vm=ALL-UNNAMED --enable-preview
 */
public class ContinuationExample {
    public static void main(String[] args) {

        var scope = new ContinuationScope("main");
        var counter = new AtomicInteger();

        var continuation = new Continuation(scope, () -> {
            System.out.println("Hello from continuation");
            var x = 0;
            x++;
            counter.incrementAndGet();
            Continuation.yield(scope);
            x++;
            System.out.println("Hello again from continuation -> " + x + " Counter " + counter.get());
            x++;
            counter.incrementAndGet();
            Continuation.yield(scope);
            System.out.println("Done from continuation -> " + x + " Counter " + counter.get());
        });

        System.out.println("Before starting continuation");
        continuation.run();
        System.out.println("After starting continuation");
        continuation.run();
        System.out.println("After starting continuation again");
        continuation.run();
    }
}
