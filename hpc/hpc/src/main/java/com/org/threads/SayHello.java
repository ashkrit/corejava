package com.org.threads;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;

import static java.lang.Thread.startVirtualThread;

public class SayHello {

    public static void main(String[] args) throws InterruptedException {
        var t = startVirtualThread(() ->
                System.out.printf("Simple -> isVirtual %s : Today Is %s%n", Thread.currentThread().isVirtual(), LocalDateTime.now()));

        t.join();

        var t2 = Thread.ofVirtual().start(() -> System.out.printf("Thread -> isVirtual %s : Today Is %s%n", Thread.currentThread().isVirtual(), LocalDateTime.now()));

        var t3 = Thread.ofVirtual().unstarted(() -> System.out.printf("NotStarted -> isVirtual %s : Today Is %s%n", Thread.currentThread().isVirtual(), LocalDateTime.now()));

        t3.start();

        try (var es = Executors.newVirtualThreadPerTaskExecutor()) {
            es.submit(() -> System.out.printf("Executor -> isVirtual %s : Today Is %s%n", Thread.currentThread().isVirtual(), LocalDateTime.now()));
        }

        t2.join();
        t3.join();


    }
}
