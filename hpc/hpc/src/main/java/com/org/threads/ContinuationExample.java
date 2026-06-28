package com.org.threads;

import jdk.internal.vm.Continuation;
import jdk.internal.vm.ContinuationScope;

/**
 * Add VM Options
 *
 * --add-exports java.base/jdk.internal.vm=ALL-UNNAMED --enable-preview
 */
public class ContinuationExample {
    public static void main(String[] args) {

        var scope = new ContinuationScope("main");

        var continuation = new Continuation(scope, () -> {
            System.out.println("Hello from continuation");
            Continuation.yield(scope);
            System.out.println("Hello again from continuation");
            Continuation.yield(scope);
            System.out.println("Done from continuation");
        });

        System.out.println("Before starting continuation");
        continuation.run();
        System.out.println("After starting continuation");
        continuation.run();
        System.out.println("After starting continuation again");
        continuation.run();
    }
}
