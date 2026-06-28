package com.org.threads.scale;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class HugeThreadApp {

    public static void main(String[] args) {
        //startVirtualThread(10_000);
        //startCacheThread(10_000);
        startPooledThread(10_000);
    }

    private static void startVirtualThread(int numberOfThread) {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            submit(numberOfThread, executor);
        }
    }

    private static void submit(int numberOfThread, ExecutorService executor) {
        IntStream.range(0, numberOfThread).forEach(i -> {
            executor.submit(() -> {
                Thread.sleep(Duration.ofSeconds(1));
                return i;
            });
        });
    }

    private static void startCacheThread(int numberOfThread) {
        try (var executor = Executors.newCachedThreadPool()) {
            submit(numberOfThread, executor);
        }
    }

    private static void startPooledThread(int numberOfThread) {
        try (var executor = Executors.newFixedThreadPool(100)) {
            submit(numberOfThread, executor);
        }
    }


}
